package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.items.ItemStack;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

public interface IPeripheralItem {

    default void register(SmartEventBusHandle handle, ItemStack stack) {
        handle.registerEventHandler((fromAddress, toAddress, eventName, eventDataStr) -> {
            if (fromAddress.equals(toAddress)) return;
            if (fromAddress.equals(handle.getAddress())) return;

            JsonValue data = JsonValue.readJSON(eventDataStr);

            switch (eventName) {
                case "cc:peripheral_discovery" -> {
                    if (data.isObject() && data.asObject().get("peripheral_type") != null) return;

                    JsonObject response = new JsonObject();
                    response.set("peripheral_type", getType());
                    handle.postEvent(fromAddress, eventName, response.toString());
                }
                case "cc:ping" -> {
                    handle.postEvent(fromAddress, "cc:pong", "{}");
                }
            }

        });
    }


    default void unregister(SmartEventBusHandle handle) {
        handle.freeHandle();
    }

    String getType();

}
