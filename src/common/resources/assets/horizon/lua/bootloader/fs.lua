-- cc.bios.chip

local SUPERBLOCK_MAGIC = "HZFS" -- horizon filesystem
local SUPERBLOCK_VERSION = 1
local SUPERBLOCK_FORMAT = "<c4 B I2 I4 I4 I4 I4 I4 I4"          -- used for packing the superblock

local FAT_ENTRY_FREE = 0
local FAT_ENTRY_EOF = 0xFFFFFFFF

local DIR_NAME_MAX = 20 -- todo maybe change
local DIR_ENTRY_SIZE = 32 -- name: 20, type: 1, size: 4, firstSector: 4, + 2 pad
local DIR_ENTRY_FORMAT = "<c20 B i4 I4 xx"

local ENTRY_TYPE_FREE = 0
local ENTRY_TYPE_FILE = 1
local ENTRY_TYPE_DIR = 2
local ENTRY_TYPE_SYMLINK = 3 -- symbolic link linking to another folder/file entry

local function zeroBytes(n)
    local t = {}
    for i = 1, n do t[i] = 0 end
    return t
end

local function bytesToString(bytes)
    local chars = {}
    for i = 1, #bytes do chars[i] = string.char(bytes[i]) end
    return table.concat(chars)
end

local function stringToBytes(str)
    local bytes = {}
    for i = 1, #str do bytes[i] = str:byte(i) end
    return bytes
end

local function splitPath(path)
    local parts = {}
    for part in path:gmatch("[^/]+") do
        parts[#parts + 1] = part
    end
    return parts
end

local fs = {}
fs.__index = fs

function fs:format(disk)
    local sectorSize = disk:getSectorSize()
    local totalSectors = disk:size()
    assert(totalSectors >= 3, "medium too small to hold a filesystem")

    local fatStartSector = 1
    local remaining = totalSectors
    local entriesPerFatSect = sectorSize // 4

    local fatSectorCount, dataSectorCount = 1, nil
    while true do
        dataSectorCount = remaining - fatSectorCount
        local required = math.ceil(dataSectorCount / entriesPerFatSect)
        if required == fatSectorCount then break end
        fatSectorCount = required
    end

    assert(dataSectorCount > 0, "medium too small to hold a filesystem")

    local dataStartSector = fatStartSector + fatSectorCount

    local zeroSector = zeroBytes(sectorSize)
    for s = fatStartSector, dataStartSector - 1 do
        disk:write(s, zeroSector)
    end

    local rootDirFirstSector = 0

    local header = string.pack(
        SUPERBLOCK_FORMAT,
        SUPERBLOCK_MAGIC,
        SUPERBLOCK_VERSION,
        sectorSize,
        totalSectors,
        fatStartSector,
        fatSectorCount,
        dataStartSector,
        dataSectorCount,
        rootDirFirstSector
    )

    local sb = stringToBytes(header)
    for i = #sb + 1, sectorSize do sb[i] = 0 end
    disk:write(0, sb)

    local _fs = setmetatable({
        disk = disk,
        sectorSize = sectorSize,
        fatStartSector = fatStartSector,
        dataStartSector = dataStartSector,
        dataSectorCount = dataSectorCount,
        rootDirFirstSector = rootDirFirstSector,
        entriesPerSector = sectorSize // DIR_ENTRY_SIZE
    }, fs)

    _fs:_setFatRaw(rootDirFirstSector, FAT_ENTRY_EOF)
    disk:write(dataStartSector + rootDirFirstSector, zeroBytes(sectorSize))

    return _fs
end

-- mount formatted disk

function fs.mount(disk)
    local sectorSize = disk:getSectorSize()
    local raw = bytesToString(disk:readBytes(0, sectorSize))
    local magic, version, storedSectorSize, totalSectors, fatStartSector, fatSectorCount, dataStartSector, dataSectorCount, rootDirFirstSector = string.unpack(SUPERBLOCK_FORMAT, raw)

    if magic ~= SUPERBLOCK_MAGIC then return nil, "not formatted" end
    if storedSectorSize ~= sectorSize then return nil, "sector size mismatch" end
    if version ~= SUPERBLOCK_VERSION then return nil, "unsupported fs version" .. version end

    return setmetatable({
        disk = disk,
        sectorSize = sectorSize,
        fatStartSector = fatStartSector,
        fatSectorCount = fatSectorCount,
        dataStartSector = dataStartSector,
        dataSectorCount = dataSectorCount,
        rootDirFirstSector = rootDirFirstSector,
        entriesPerSector = sectorSize // DIR_ENTRY_SIZE
    }, fs)
end

-- fat chain allocation

function fs:_fatByteOffset(dataIndex)
    return self.fatStartSector * self.sectorSize + dataIndex * 4
end

function fs:_getFatRaw(dataIndex)
    local bytes = self.disk:readBytes(self:_fatByteOffset(dataIndex), 4)
    return string.unpack("<I4", bytesToString(bytes))
end

function fs:_setFatRaw(dataIndex, raw)
    self.disk:writeBytes(self:_fatByteOffset(dataIndex), stringToBytes(string.pack("<I4", raw)))
end

function fs:_allocateChain(count)
    if count <= 0 then return nil, "invalid count" end
    local free = {}
    for i = 0, self.dataSectorCount - 1 do
        if self:_getFatRaw(i) == FAT_ENTRY_FREE then
            free[#free + 1] = i
            if #free == count then break end
        end
    end
    if #free < count then return nil, "not enough free space" end

    for i = 1, #free - 1 do
        self:_setFatRaw(free[i], free[i+1]+1)
    end
    self:_setFatRaw(free[#free], FAT_ENTRY_EOF)
    return free[1]
end

function  fs:_freeChain(startDataIndex)
    if not startDataIndex then return end
    local idx = startDataIndex

    while true do
        local raw = self:_getFatRaw(idx)
        self:_setFatRaw(idx, FAT_ENTRY_FREE)
        if raw == FAT_ENTRY_EOF then break end
        idx = raw - 1
    end
end

function fs:_extendChain(startDataIndex)
    local newIndex, err = self:_allocateChain(1)
    if not newIndex then return nil, err end

    local last = startDataIndex
    while true do
        local raw = self:_getFatRaw(last)
        if raw == FAT_ENTRY_EOF then break end
        last = raw - 1
    end

    self:_setFatRaw(last, newIndex+1)
    self:_setFatRaw(newIndex, FAT_ENTRY_EOF)
    self.disk:write(self.dataStartSector + newIndex, zeroBytes(self.sectorSize))
    return newIndex
end

-- directories

function fs:_entryOffset(dataIndex, slot)
    return (self.dataStartSector + dataIndex) * self.sectorSize + slot * DIR_ENTRY_SIZE
end

function fs:_readEntry(dataIndex, slot)
    local bytes = self.disk:readBytes(self:_entryOffset(dataIndex,slot), DIR_ENTRY_SIZE)
    local name, entryType, size, firstSectorRaw = string.unpack(DIR_ENTRY_FORMAT, bytesToString(bytes))

    return {
        name = (name:gsub("%z+$", "")),
        entryType = entryType,
        size = size,
        firstSector = (firstSectorRaw > 0) and (firstSectorRaw - 1) or nil,
        used = entryType ~= ENTRY_TYPE_FREE
    }
end

function fs:_writeEntry(dataIndex, slot, name, entryType, size, firstDataIndex)
    local firstSectorRaw = firstDataIndex and (firstDataIndex + 1) or 0
    local packed = string.pack(DIR_ENTRY_FORMAT, name, entryType, size, firstSectorRaw)
    self.disk:writeBytes(self:_entryOffset(dataIndex, slot), stringToBytes(packed))
end


-- visit every fucking slot (fs list)
function fs:_forEachSlot(dirFirstDataIndex, callback)
    local idx = dirFirstDataIndex

    while true do
        for slot = 0, self.entriesPerSector - 1 do
            local entry = self:_readEntry(idx, slot)
            if callback(idx, slot, entry) then
                return idx, slot, entry
            end
        end
        local raw = self:_getFatRaw(idx)
        if raw == FAT_ENTRY_EOF then break end
        raw = raw - 1
    end
    return nil
end

function fs:_findEntry(dirFirstDataIndex, name)
    local _,_, entry = self:_forEachSlot(dirFirstDataIndex, function(_,_, e)
        return e.used and e.name == name
    end)
    return entry
end

function fs:_findFreeSlotOrExtend(dirFirstDataIndex)
    local dataIndex, slot = self:_forEachSlot(dirFirstDataIndex, function(_,_,e)
        return not e.used
    end)

    if dataIndex then return dataIndex, slot end

    local newIndex, err = self:_extendChain(dirFirstDataIndex)
    if not newIndex then return nil, nil, err end
    return newIndex, 0
end

-- path res

function fs:_resolveParent(path)
    local parts = splitPath(path)
    if #parts == 0 then return nil, nil, "empty path" end

    local dirIndex = self.rootDirFirstSector
    for i = 1, #parts - 1 do
        local entry = self:_findEntry(dirIndex, parts[i])
        if not entry then return nil, nil, "no such directory: " .. parts[i] end
        if entry.entryType ~= ENTRY_TYPE_DIR then return nil, nil, parts[i] .. " is not a directory" end
        dirIndex = entry.firstSector
    end
    return dirIndex, parts[#parts]
end

-- finally, finally, public api

function fs:exists(path)
    if path == "" or path == "/" then return true end
    local dirIndex, name = self:_resolveParent(path)
    if not dirIndex then return false end
    return self:_findEntry(dirIndex, name) ~= nil
end

function fs:isDir(path)
    if path == "" or path == "/" then return true end
    local dirIndex, name = self:_resolveParent(path)
    if not dirIndex then return false end
    local entry = self:_findEntry(dirIndex, name)
    return entry ~= nil and entry.entryType == ENTRY_TYPE_DIR
end

-- future symbolic link support
function fs:isSymLink(path)
    if path == "" or path == "/" then return true end
    local dirIndex, name = self:_resolveParent(path)
    if not dirIndex then return false end
    local entry = self:_findEntry(dirIndex, name)
    return entry ~= nil and entry.entryType == ENTRY_TYPE_SYMLINK
end

function fs:getSize(path)


end

function fs:list(path)

end

function fs:makeDir(path)


end

function fs:delete(path, recursive)


end

function fs:readFile(path)

end

function writeFile(path, contents)

end

function fs:getCapacity()

end

function fs:getFreeSpace()

end

-- mount registry or named disks
-- hdd, cd-rom, drive-0, C: etc
-- anything that says name is name of drive
-- anything that has path includes drive name, such as, hdd/whatever.json

local Mounts = {}
Mounts.__index = Mounts

function Mounts.new()
    return setmetatable({ drives = {} }, Mounts)
end

function Mounts:mount(name, disk, formatIfNeeded)

end

function Mounts:unmount(name)

end

-- send a call to a specific api on fs on a specific mount
function Mounts:_dispatch(path, methodName, ...)

end

function Mounts:list(path)

end

function Mounts:exists(path)

end

function Mounts:isDir(path)

end

function Mounts:getSize(path) end
function Mounts:makeDir(path) end
function Mounts:delete(path, r) end
function Mounts:readFile(path) end
function Mounts:writeFile(path, c) end

function Mounts:getFreeSpace(name) end
function Mounts:getCapacity(name) end

function Mounts:moveFile(path) end

return {
    fs = fs,
    Mounts = Mounts,
    ENTRY_TYPE_FILE = ENTRY_TYPE_FILE,
    ENTRY_TYPE_DIR = ENTRY_TYPE_DIR
}