print("Hello from BIOS :)")

local function bytesToString(bytes)
    local charTable = {}
    for i = 1, #bytes do
        charTable[i] = string.char(bytes[i])
    end
    return table.concat(charTable)
end

--function loadJsonLib()
--    local initSize = cc.bios.chip.readInt(0)
--    local jsonLibSize = cc.bios.chip.readInt(4 + initSize)
--
--    local jsonLibBytes = cc.bios.chip.getBytes(8 + initSize, jsonLibSize)
--    local jsonLibCode = bytesToString(jsonLibBytes)
--
--    jsonLib = loadstring(jsonLibCode, "qjson.lua")()
--    return jsonLib
--end

local __font_table = {}

if _G["cc"] ~= nil then return end

local function loadFont()
    local initSize = cc.bios.chip.readInt(0)
    local jsonLibSize = cc.bios.chip.readInt(4 + initSize)
    local fontSize = cc.bios.chip.readInt(8 + initSize + jsonLibSize)
    local fontBytes = cc.bios.chip.getBytes(12 + initSize + jsonLibSize, fontSize)

    __font_table["chr_width"] = 5
    __font_table["chr_height"] = 5

    for chr = 0, 99 do
        __font_table[chr] = { chr = chr, x = ((chr % 10) * 6) + 2, y = (math.floor(chr / 10) * 6) + 2 }
        print(__font_table[chr])
    end

    return cc.imageio.fromBytes(fontBytes)
end

local __font_image = loadFont()

local function getGlyph(chr)
    if type(chr) == "string" then
        print(chr, string.byte(chr), string.byte(chr) - 32)
        return getGlyph(string.byte(chr) - 32)
    end
    return __font_table[chr]
end

local function getGlyphs(str)
    local glyphs = {}
    for i = 1, #str do
        local chr = string.sub(str, i, i)
        table.insert(glyphs, getGlyph(chr))
    end
    return glyphs
end

print(getGlyphs("Hi"))

--local jsonLib = loadJsonLib()

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
    if #foundBootloaders then

    end
end

findBootloaders()


--handle.registerEventHandler(
--        function (from, to, eventName, dataStr)
--            if from == computerAddress then
--                return
--            end
--
--            print("Caught " .. eventName .. " from " .. from)
--        end
--)
--
--handle.postEvent(
--        "*",
--        "cc:peripheral_discovery",
--        jsonLib.encode({})
--)

