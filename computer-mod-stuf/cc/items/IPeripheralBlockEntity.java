package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import me.zombii.horizon.common.cc.computer.peripherals.PeripheralInstance;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import party.iroiro.luajava.Lua;

public interface IPeripheralBlockEntity extends IPeripheral {

    default PeripheralInstance registerBE(Lua L, SmartEventBusHandle handle, BlockEntity entity) {
        return new PeripheralInstance(L, this, handle);
    }

    default boolean register(Lua L, SmartEventBusHandle handle, BlockEntity blockEntity) {
        handle.registerEventHandler((fromAddress, toAddress, eventName, eventDataStr) -> {
            if (fromAddress.equals(toAddress)) return;
            if (fromAddress.equals(handle.getAddress())) return;

            JsonValue data = JsonValue.readJSON(eventDataStr);

            switch (eventName) {
                case "cc:peripheral_discovery" -> {
                    if (data.isObject() && data.asObject().get("peripheral_type") != null) return;

                    JsonObject response = new JsonObject();
                    response.set("peripheral_type", getPeripheralType());
                    handle.postEvent(fromAddress, eventName, response.toString());
                }
                case "cc:ping" -> {
                    handle.postEvent(fromAddress, "cc:pong", "{}");
                }
            }
        });
        return false;
    }

}
