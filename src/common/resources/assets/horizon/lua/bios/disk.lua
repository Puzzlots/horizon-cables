-- bios defines lower level disk operations
-- bootloader defined fs. functions
local bit = bit or bit32

-- uses ints to write data faster


local disk = {}
disk.__index = disk

local SECTOR_SIZE = 512
local BYTES_PER_INT = 4

function disk.new(p, capacity, sectorSize)
    sectorSize = sectorSize or SECTOR_SIZE

    return setmetatable({
        api = p,
        capacityBytes = capacity,
        sectorSize = sectorSize,
        sectorCount = capacity // sectorSize
    }, disk)
end


-- returns a boolean on whether the sector argument is correct and within the count
function disk:isValidSector(sector)
    return type(sector) == "number" and sector >= 0 and sector < self.sectorCount
end

function disk:checkSector(sector)
    if not self:isValidSector(sector) then
        error(("sector %s out of range (0-%d)"):format(tostring(sector), self.sectorCount - 1))
    end
end

function disk:read(sector)
    self:checkSector(sector)
    local base = sector * self.sectorSize
    local out, i = {}, 1

    local fullInts = self.sectorSize // BYTES_PER_INT

    for w = 0, fullInts - 1 do
        local value = self.api:readInt(base + w * BYTES_PER_INT)
        out[i] = bit.band(bit.rshift(value,24), 0xFF)
        out[i+1] = bit.band(bit.rshift(value, 16), 0xFF)
        out[i+2] = bit.band(bit.rshift(value, 8), 0xFF)
        out[i+3] = bit.band(value, 0xFF)
        i = i + 4
    end

    local remainder = self.sectorSize % BYTES_PER_INT
    for b = 0, remainder - 1 do
        out[i] = self.api.readByte(base + fullInts * BYTES_PER_INT + b)
        i = i + 1
    end
    return out
end

function disk:write(sector, data)
    self:checkSector(sector)
    assert(#data == self.sectorSize, ("data must be exactly %d bytes, got %d"):format(self.sectorSize, #data))

    local base = sector * self.sectorSize
    local fullInts = self.sectorSize // BYTES_PER_INT
    local i = 1

    for w = 0, fullInts - 1 do
        local b1, b2, b3, b4 = data[i], data[i+1], data[i+2], data[i+3]
        i = i+4
        local value = bit.bor(bit.lshift(b1, 24), bit.bor(bit.lshift(b2, 16), bit.bor(bit.lshift(b3, 8), b4)))
        self.api:writeInt(base+w*BYTES_PER_INT, value)
    end

    local remainder = self.sectorSize % BYTES_PER_INT
    for b = 0, remainder - 1 do
        self.api:writeByte(base + fullInts * BYTES_PER_INT + b, data[i])
        i = i + 1
    end

    return true
end

function disk:readBytes(offset, length)
    assert(offset >= 0 and length >= 0 and offset + length <= self.capacityBytes, "read out of bounds")
    if length == 0 then return {} end

    local startSector = offset // self.sectorSize
    local endSector = (offset + length - 1) // self.sectorSize
    local result, pos = {}, 1

    for sector = startSector, endSector do
        local sectorData = self:read(sector)
        local sectorBase = sector * self.sectorSize

        for localOffset = 1, self.sectorSize do
            local globalOffset = sectorBase + localOffset - 1
            if globalOffset >= offset and globalOffset < offset + length  then
                result[pos] = sectorData[localOffset]
                pos = pos + 1
            end
        end
    end

    return result
end

function disk:writeBytes(offset, data)
    local length = #data
    assert(offset >= 0 and offset + length <= self.capacityBytes, "write out of bounds")
    if length == 0 then return true end

    local startSector = offset // self.sectorSize
    local endSector = (offset + length - 1) // self.sectorSize
    local srcPos = 1

    for sector = startSector, endSector do
        local sectorBase = sector * self.sectorSize
        local sectorData = self:read(sector)

        for localOffset = 1, self.sectorSize do
            local globalOffset = sectorBase + localOffset - 1
            if globalOffset >= offset and globalOffset < offset + length then
                sectorData[localOffset] = data[srcPos]
                srcPos = srcPos + 1
            end
        end

        self:write(sector, sectorData)
    end

    return true
end

function disk:format()
    local zeroSector = {}
    for i = 1, self.sectorSize do zeroSector[i] = 0 end

    for sector = 0, self.sectorCount - 1 do
        self:write(sector, zeroSector)
    end
    return true
end

function disk:checksumSector(sector)
    local data = self:read(sector)
    local acc = 0
    for i = 1, #data do
        acc = bit.band(bit.bxor(acc, data[i]), 0xFF)
    end
    return acc
end

function disk:maxSize()
    return self.capacityBytes
end

function disk:size()
    return self.sectorCount
end

function disk:getSectorSize()
    return self.sectorSize
end

return {
    disk = disk,
    SECTOR_SIZE = SECTOR_SIZE
}