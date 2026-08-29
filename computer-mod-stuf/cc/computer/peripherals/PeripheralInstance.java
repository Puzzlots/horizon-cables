package me.zombii.horizon.common.cc.computer.peripherals;

import me.zombii.horizon.common.cc.items.IPeripheral;
import me.zombii.horizon.common.cc.lua.LuaCCLib;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.jspecify.annotations.Nullable;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

public class PeripheralInstance {

    private final Lua L;
    private final IPeripheral peripheral;
    private final SmartEventBusHandle handle;
    private final LuaValue table;

    public PeripheralInstance(
            Lua L,
            IPeripheral peripheral,
            SmartEventBusHandle handle
    ) {
        this.L = L;
        this.peripheral = peripheral;
        this.handle = handle;

        L.newTable();
        this.table = L.get();
    }

    public boolean isConnected() {
        return !handle.isFree();
    }

    public Lua getLua() {
        return L;
    }

    public IPeripheral getPeripheral() {
        return peripheral;
    }

    public SmartEventBusHandle getHandle() {
        return handle;
    }

    private LuaFunction wrap(LuaFunction function) {
        return (L, args) -> {
            if (!isConnected()) {
                L.error("Peripheral " + peripheral.getPeripheralID() + " " + peripheral.getPeripheralType() + " got disconnected!");
                return LuaCCLib.EMPTY;
            }
            return function.call(L, args);
        };
    }

    private int tableIdx = -1;

    public void begin() {
        L.push(table);
        tableIdx = L.getTop();
    }

    public void addFunction(LuaFunction function, String name) {
        L.push(wrap(function));
        L.setField(tableIdx, name);
    }

    public void addValue(LuaValue value, String name) {
        L.push(value);
        L.setField(tableIdx, name);
    }

    public void end() {
        L.pop(1);
        tableIdx = -1;
    }

    public LuaValue getApi() {
        return table;
    }

}
