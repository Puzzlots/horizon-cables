print("Hello from BIOS :)")

if (table.unpack == nil) then
    table.unpack = unpack
end
if (bit32 == nil) then
    bit32 = bit
end

local function bytesToString(bytes)
    local charTable = {}
    for i = 1, #bytes do
        charTable[i] = string.char(bit32.band(bytes[i], 0xFF))
    end
    return table.concat(charTable)
end

if _G["cc"] == nil then return end

local function getBundledEntries()
    local entries = {}

    local entryCount = cc.bios.chip.readInt(0)
    local offs = 4

    for _ = 1, entryCount do
        local nameLen = cc.bios.chip.readInt(offs)
        local name = bytesToString(cc.bios.chip.getBytes(offs + 4, nameLen))
        offs = offs + nameLen + 4
        local size = cc.bios.chip.readInt(offs)
        local addr = cc.bios.chip.readInt(offs + 4)
        offs = offs + 8

        entries[name] = {
            name = name,
            size = size,
            addr = addr
        }
    end

    for _, v in pairs(entries) do
        v.addr = v.addr + offs
    end

    return entries
end

local __file_entries = getBundledEntries()

local function readEntry(name)
    if __file_entries[name] == nil then
        return nil
    end
    return cc.bios.chip.getBytes(__file_entries[name].addr, __file_entries[name].size)
end

local function loadfile(filename, env)
    local fn = loadstring(bytesToString(readEntry(filename)), filename)
    setfenv(fn, env)
    return fn
end

local function dofile(filename)
    return loadfile(filename, _G)()
end

local function require(module)
    return loadfile(module .. ".lua", _G)()
end

local modEnv = {
    math = math,
    bit = bit,
    bit32 = bit32,
    string = string,
    table = table,
    require = require,
    dofile = dofile,
    loadfile = loadfile,
    readEntry = readEntry,
    coroutine = coroutine,
    cc = cc,
    type = type,
    assert = assert,
    error = error,
    ipairs = ipairs,
    pairs = pairs,
    print = print
}

modEnv._G = modEnv

local fontLib = loadfile("font.lua", modEnv)()

local glyph = fontLib.getGlyph("H")

print(bit32.band(fontLib.__font_image.getPixel(glyph.x + 1, glyph.y), 0x0000FF))
--print(getGlyphs("Hi"))

local handle = cc.eventBus.getNewAddress()
local computerAddress = handle.getAddress()

print("Computer IP: " .. computerAddress)

local bootLoaderEntry = {
    storageDevice = nil,
    initCodeSize = 0,
    initCodeAddress = 0
}

function bootLoaderEntry:getInitCode()
    if self.__initCode == nil then
        self.__initCode = bytesToString(self.storageDevice.api.getBytes(self.initCodeAddress + 4, self.initCodeSize))
    end
    return self.__initCode
end

function bootLoaderEntry:loadInit()
    return loadstring(self:getInitCode(), "init.lua")
end

local function findBootLoader(storage)
    local hasBootloader = storage.api.readInt(0) == 0x55AA
    if not hasBootloader then
        return nil
    end
    local initAddress = storage.api.readInt(4)
    local initSize = storage.api.readInt(initAddress)

    return setmetatable(
        {
                storageDevice = storage,
                initCodeSize = initSize,
                initCodeAddress = initAddress,
                __initCode = nil
            },
        bootLoaderEntry
        )
end

local function findBootloaders()
    local hardDisks = cc.peripherals.internal.findPeripherals("cc:hard-disk-drive", "cc:storage-device")
    local floppyDisks = cc.peripherals.internal.findPeripherals("cc:floppy-disk", "cc:storage-device")
    local compactDiscs = cc.peripherals.internal.findPeripherals("cc:compact-disc", "cc:storage-device")
    local bootableMedia = table.pack(
            table.unpack(hardDisks),
            table.unpack(floppyDisks),
            table.unpack(compactDiscs)
    )

    local foundBootloaders = {}
    if #bootableMedia ~= 0 then
        print(#bootableMedia, "storage disks found!")
        for i = 1, #bootableMedia do
            local id = bootableMedia[i].peripheral_id
            local size = bootableMedia[i].api.getSize()
            local bLoader = findBootLoader(bootableMedia[i])
            if bLoader ~= nil then
                table.insert(foundBootloaders, bLoader)
                --print(tostring(i) .. " - Found", id, "that has a bootloader! Loader Size:", bLoader.initCodeSize, "Address:", bLoader.initCodeAddress)
            else
                --print(tostring(i) .. " - Found", id, " no bootloader, Disk Size", size)
            end
        end
    end
    print("Found", #foundBootloaders, "bootloaders.")
    if #foundBootloaders ~= 0 then
        return foundBootloaders
    end
    return {}
end

--local biosApi = {
--    __internal_cc = cc
--}
--
--function biosApi:getDiskApi()
--end
--
--function biosApi:getBiosChip()
--    return self.__internal_cc.bios.chip
--end
--
--function biosApi:getPeripheralApi()
--    return self.__internal_cc.peripherals
--end
--
--cc = nil

--local bootEntry = findBootloaders()[1]
--assert(type(bootEntry) == "table")
--setmetatable(bootEntry, bootLoaderEntry)
--local initFunc = bootEntry:loadInit()
--initFunc(
--    {
--        device = bootEntry.storageDevice,
--        size = bootEntry.initCodeSize,
--        address = bootEntry.initCodeAddress
--    },
--    biosApi
--);
