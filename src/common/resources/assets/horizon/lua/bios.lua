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

local storagePeripheralAPI = {}
local eventBuffer = {}

--function storagePeripheralAPI.open()
--    handle.postEvent("*", "cc:peripheral_discovery", "{}")
--    for i, v in ipairs(eventBuffer) do
--        if v.eType == "cc:peripheral_discovery" then
--            if v.data.peripheral_type == "cc:storage-device" then
--                table.remove(eventBuffer, i)
--                storagePeripheralAPI["ip"] = v.ip
--                print(v)
--                break
--            end
--        end
--    end
--    handle.postEvent(
--            storagePeripheralAPI["ip"],
--            "cc:session",
--            "{\"status\":\"request\"}"
--    )
--    for i, v in ipairs(eventBuffer) do
--        if v.eType == "cc:session" then
--            if v.data.status == "success" then
--                table.remove(eventBuffer, i)
--                break
--            end
--        end
--    end
--end
--function storagePeripheralAPI.readInt(position)
--    handle.postEvent(
--            storagePeripheralAPI["ip"],
--            "cc:call",
--            jsonLib.encode({
--                status = "request",
--                fn_name = "readInt",
--                fn_args = {0}
--            })
--    )
--    for i, v in ipairs(eventBuffer) do
--        if v.eType == "cc:call" then
--            if v.data.status == "response" then
--                table.remove(eventBuffer, i)
--                print(v.data.output)
--                break
--            end
--        end
--    end
--end
--function storagePeripheralAPI.close()
--    handle.postEvent(
--            storagePeripheralAPI["ip"],
--            "cc:session",
--            "{\"status\":\"terminate\"}"
--    )
--    for i, v in ipairs(eventBuffer) do
--        if v.eType == "cc:session" then
--            if v.data.status == "terminated" then
--                table.remove(eventBuffer, i)
--                break
--            end
--        end
--    end
--end

local computerAddress = handle.getAddress()

print("Computer IP: " .. computerAddress)

--storagePeripheralAPI.open()
--print(storagePeripheralAPI.readInt(0))
--storagePeripheralAPI.close()

handle.registerEventHandler(
        function (from, to, eventName, dataStr)
            if from == computerAddress then
                return
            end

            print("Caught " .. eventName .. " from " .. from)
            table.insert(eventBuffer, { eType = eventName, ip = from, data = jsonLib.decode(dataStr)})
        end
)

handle.postEvent(
        "*",
        "cc:peripheral_discovery",
        jsonLib.encode({})
)
