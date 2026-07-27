package me.zombii.horizon.common.cc.items;

import me.zombii.horizon.common.cc.computer.peripherals.PeripheralInstance;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import party.iroiro.luajava.Lua;

public interface IPeripheral {

    default PeripheralInstance register(Lua L, SmartEventBusHandle handle) {
        return new PeripheralInstance(L, this, handle);
    }

    default void unregister(SmartEventBusHandle handle) {
        handle.freeHandle();
    }

    String getPeripheralID();
    String getPeripheralType();

}
