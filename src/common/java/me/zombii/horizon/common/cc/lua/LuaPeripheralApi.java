package me.zombii.horizon.common.cc.lua;

import finalforeach.cosmicreach.items.ItemStack;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.cc.computer.peripherals.PeripheralInstance;
import me.zombii.horizon.common.cc.items.IPeripheral;
import me.zombii.horizon.common.cc.items.IPeripheralItem;
import me.zombii.horizon.common.cc.lua.bus.AddressableLuaEventBus;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.apache.commons.lang3.function.TriFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.value.LuaValue;

public class LuaPeripheralApi {

    public static void push(Lua L, BlockEntityDevComputer be) {
        L.newTable();
        int t = L.getTop();

        pushInternal(L, be);
        L.setField(t, "internal");
    }

    public static void pushInternal(Lua L, BlockEntityDevComputer be) {
        L.newTable();
        int t = L.getTop();
        pushFunction(L, t, be, LuaPeripheralApi::findPeripheralsInternal, "findPeripherals");
    }

    private static void pushFunction(
            Lua lua,
            int idx,
            BlockEntityDevComputer device,
            TriFunction<BlockEntityDevComputer, Lua, LuaValue[], LuaValue[]> f,
            String name
    ) {
        lua.push((L, args) -> f.apply(device, L, args));
        lua.setField(idx, name);
    }

    public static LuaValue[] findPeripheralsInternal(BlockEntityDevComputer device, Lua L, LuaValue[] args) {
        String requestedId = null;
        String requestedType = null;
        if (args.length > 2) {
            throw new LuaException(
                    LuaException.LuaError.RUNTIME,
                    "'peripherals.internal.findInternalPeripherals' requires 0-2 arguments."
            );
        }
        if (args.length == 1 || args.length == 2) {
            LuaUtil.checkArg(
                    "peripherals.internal.findPeripherals",
                    args, 0,
                    Lua.LuaType.STRING, Lua.LuaType.NIL
            );
            if (args[0] != null && args[0].type() != Lua.LuaType.NIL) {
                requestedId = args[0].toString();
            }
            if (args.length == 2) {
                LuaUtil.checkArg(
                        "peripherals.internal.findPeripherals",
                        args, 1,
                        Lua.LuaType.STRING, Lua.LuaType.NIL
                );
                if (args[1] != null && args[1].type() != Lua.LuaType.NIL) {
                    requestedType = args[1].toString();
                }
            }
        }

        L.newTable();
        int t = L.getTop();

        PeripheralInstance[] instances = device.getPeripheralInstances();

        int number = 1;
        for (PeripheralInstance instance : instances) {
            if (instance == null) continue;

            IPeripheral peripheral = instance.getPeripheral();

            if (requestedId != null && !peripheral.getPeripheralID().equals(requestedId)) continue;
            if (requestedType != null && !peripheral.getPeripheralType().equals(requestedType)) continue;

            L.newTable();
            int p = L.getTop();
            L.push(peripheral.getPeripheralID());
            L.setField(p, "peripheral_id");
            L.push(peripheral.getPeripheralType());
            L.setField(p, "peripheral_type");

            L.push(instance.getHandle().getAddress());
            L.setField(p, "address");

            L.push(instance.getApi());
            L.setField(p, "api");

            L.rawSetI(t, number++);
        }

        return new LuaValue[]{L.get()};
    }

}
