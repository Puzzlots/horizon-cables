print("Hello from BIOS :)")

function bytesToString(bytes)
    local charTable = {}
    for i = 1, #bytes do
        charTable[i] = string.char(bytes[i])
    end
    return table.concat(charTable)
end

function loadJsonLib()
    initSize = cc.bios.chip.readInt(0)
    jsonLibSize = cc.bios.chip.readInt(initSize + 4)

    jsonLibBytes = cc.bios.chip.getBytes(initSize + 4 + 4, jsonLibSize)
    jsonLibCode = bytesToString(jsonLibBytes)

    jsonLib = load(jsonLibCode, "json.lua", "t")()
    return jsonLib
end
jsonLib = loadJsonLib()

local handle = cc.eventBus.getNewAddress()
local computerAddress = handle.getAddress()

print("Computer IP: " .. computerAddress)

local internalPeripherals = cc.peripherals.findInternalPeripherals("cc:storage-device")
if #internalPeripherals ~= 0 then
end

handle.registerEventHandler(
        function (from, to, eventName, dataStr)
            if from == computerAddress then
                return
            end

            print("Caught " .. eventName .. " from " .. from)
        end
)

handle.postEvent(
        "*",
        "cc:peripheral_discovery",
        jsonLib.encode({})
)
