local inner_peripherals = peripherals.getInnerPeripherals()
--
-- { id = 0, type = "horizon:hard-drive", place = "inner" }
--
local event_bus = motherboard.getInternalEventBus()
local utils = motherboard.getBiosUtils()

local ebus = {}

event_bus.post({
    id = -1,
    type = "bios:peripheral_discovery"
})
{
    id = 0,
    type = "horizon:hard-drive"
}

local event = event_bus.pull()
print(event.id)
print(event.type)

local event_nilable = event_bus.getEvent() -- can be nil
if event_nilable ~= nil then
    print(event_nilable.id)
    print(event_nilable.type)
end

while true do
    local event_nilable = event_bus.getEvent() -- can be nil
    if event_nilable ~= nil then
        print(event_nilable.id)
        print(event_nilable.type)
    end

    do_stuff()
end

event_bus.register("bios:peripheral_discovery", function (event)
    local peripheral_id = event.id
    local peripheral_type = event.type
end)
