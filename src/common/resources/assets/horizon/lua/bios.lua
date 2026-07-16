print("Hello from BIOS :)")

local innerEventBus = cc.eventBus

innerEventBus.register(
    "cc:peripheral_discovery",
    function (eventName, eventData)
        print("Caught " .. eventName)
        print(eventData)
    end
)

innerEventBus.post(
    "cc:peripheral_discovery",
[[{
    "peripheral_address": "127.0.0.1",
    "peripheral_type": "cc:bios"
}]]
)
