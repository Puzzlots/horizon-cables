package me.zombii.horizon.common.cc.lua;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

import java.util.Arrays;
import java.util.function.*;

public class LuaUtil {

    public static LuaFunction newRunnable(Runnable runnable) {
        return (L, args) -> {
            runnable.run();
            return LuaCCLib.EMPTY;
        };
    }

    public static LuaFunction newByteConsumer(Consumer<Byte> supplier) {
        return (L, args) -> {
            long numberA = args[0].toInteger();
            supplier.accept((byte) (numberA & 0xFF));
            return LuaCCLib.EMPTY;
        };
    }

    public static LuaFunction newNumberGetter(Supplier<Number> supplier) {
        return (L, args) -> {
            L.push(supplier.get());
            return new LuaValue[]{L.get()};
        };
    }

    public static LuaFunction newBooleanGetter(Supplier<Boolean> supplier) {
        return (L, args) -> {
            L.push(supplier.get());
            return new LuaValue[]{L.get()};
        };
    }

    public static boolean checkArg(LuaValue[] args, int idx, Lua.LuaType expected) {
        return args[idx].type() != expected;
    }

    public static void checkArg(String name, LuaValue[] args, int idx, Lua.LuaType... expected) {
        boolean found = false;
        for (Lua.LuaType luaType : expected) {
            if (args[idx].type().equals(luaType)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new LuaException(LuaException.LuaError.RUNTIME, "Argument #" + (idx + 1) + " of '" + name + "' must be a " + Arrays.toString(expected) + ", not '" + args[idx].type() + "'");
        }
    }

    public static void checkBounds(String name, int idx, int size, boolean oneIndexed, int argIdx) {
        if (oneIndexed) idx -= 1;

        if (idx < 0 || idx >= size)
            throw new LuaException(LuaException.LuaError.RUNTIME, "Index out of bounds for Argument #" + (argIdx + 1) + " of '" + name + "'. Tried getting element '" + (idx + (oneIndexed ? 1 : 0)) + "' from array sized '" + size + "'");
    }
}
