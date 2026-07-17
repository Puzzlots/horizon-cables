package me.zombii.horizon.common.cc.lua;

import finalforeach.cosmicreach.items.ItemStack;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.cc.items.IPeripheralItem;
import me.zombii.horizon.common.cc.lua.bus.AddressableLuaEventBus;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.apache.commons.lang3.function.TriFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

public class LuaPeripheralApi {

    public static void push(Lua L, BlockEntityDevComputer be) {
        L.newTable();
        int t = L.getTop();

        pushFunction(L, t, be, LuaPeripheralApi::findInternalByType, "findInternalPeripherals");
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

    public static LuaValue[] findInternalByType(BlockEntityDevComputer device, Lua L, LuaValue[] args) {
        String requestedType = null;
        if (args.length == 1) {
            LuaStorageApi.checkArg(L, "peripherals.findInternalPeripherals", args, 0, Lua.LuaType.STRING);
            requestedType = args[0].toString();
        }

        L.newTable();
        int t = L.getTop();

        AddressableLuaEventBus bus = device.getInternalBus();

        for (int i = 0; i < device.getContainer().numberOfSlots; i++) {
            ItemStack stack = device.getContainer().getSlot(i).getItemStack();
            if (stack == null) continue;
            if (!(stack.getItem() instanceof IPeripheralItem peripheral)) continue;

            if (requestedType != null && !peripheral.getType().equals(requestedType)) continue;

            L.newTable();
            int p = L.getTop();
            L.push(peripheral.getType());
            L.setField(p, "id");

            SmartEventBusHandle handle = bus.getNewAddress();
            L.push(handle.getAddress());
            L.setField(p, "address");

            boolean returnedApiTable = peripheral.register(L, handle, stack);
            if (returnedApiTable) {
                L.setField(p, "api");
            }
            L.rawSetI(t, i + 1);
        }
        return new LuaValue[]{L.get()};
    }

}
