package me.zombii.horizon.common.cc.lua;

import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import org.jspecify.annotations.Nullable;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

public class LuaCCLib {

    public static final LuaValue[] EMPTY = new LuaValue[0];

    public static void inject(BlockEntityDevComputer computer) {
        Lua lua = computer.getLuaState();
        LuaEventBus internalBus = computer.getInternalPeripheralEventBus();

        lua.openLibraries();
        lua.pushNil();
        lua.setGlobal("java");

        lua.register("print", printFunction());

        lua.newTable();
        int t = lua.getTop();
        internalBus.push(lua);
        lua.setField(t, "eventBus");

        lua.setGlobal("cc");
    }

    public static LuaFunction printFunction() {
        return (L, args) -> {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                LuaValue arg = args[i];
                buf.append(arg);
                if (i < args.length - 1) {
                    buf.append(" ");
                }
            }
            buf.append("\n");
            System.out.print(buf);
            return EMPTY;
        };
    }

    public static LuaFunction getEventBus(BlockEntityDevComputer computer) {
        return (L, args) -> {
             return new LuaValue[0];
        };
    }

}
