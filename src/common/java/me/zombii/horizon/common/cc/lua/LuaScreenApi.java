package me.zombii.horizon.common.cc.lua;

import me.zombii.horizon.common.cc.computer.peripherals.PeripheralInstance;
import me.zombii.horizon.common.cc.display.ICCScreen;
import org.apache.commons.lang3.function.TriFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

public class LuaScreenApi {

    private static LuaFunction add(
            ICCScreen img,
            TriFunction<ICCScreen, Lua, LuaValue[], LuaValue[]> f
    ) {
        return (L, args) -> f.apply(img, L, args);
    }

    public static void push(PeripheralInstance instance, ICCScreen screen) {
        instance.addFunction(LuaUtil.newNumberGetter(screen::getWidth), "getWidth");
        instance.addFunction(LuaUtil.newNumberGetter(screen::getHeight), "getHeight");
        instance.addFunction(add(screen, LuaScreenApi::getPixel), "getPixel");
        instance.addFunction(add(screen, LuaScreenApi::setPixel), "setPixel");
        instance.addFunction(LuaUtil.newByteConsumer(screen::fill), "fill");
        instance.addFunction(LuaUtil.newRunnable(screen::swap), "swap");
    }

    public static LuaValue[] getPixel(ICCScreen screen, Lua L, LuaValue[] args) {
        if (args.length != 2) {
            throw new LuaException(
                    LuaException.LuaError.RUNTIME,
                    "'imageio.getPixel' requires 2 arguments!"
            );
        }
        LuaUtil.checkArg(
                "screen.getPixel",
                args, 0,
                Lua.LuaType.NUMBER
        );
        LuaUtil.checkArg(
                "screen.getPixel",
                args, 1,
                Lua.LuaType.NUMBER
        );
        int x = Math.toIntExact(args[0].toInteger() & 0xFFFFFFFFL);
        int y = Math.toIntExact(args[1].toInteger() & 0xFFFFFFFFL);
        LuaUtil.checkBounds("screen.getPixel", x, screen.getWidth(), true, 0);
        LuaUtil.checkBounds("screen.getPixel", y, screen.getHeight(), true, 1);

        L.push(screen.getPixel(x - 1, y - 1));

        return new LuaValue[]{L.get()};
    }

    public static LuaValue[] setPixel(ICCScreen image, Lua L, LuaValue[] args) {
        if (args.length != 3) {
            throw new LuaException(
                    LuaException.LuaError.RUNTIME,
                    "'screen.setPixel' requires 3 arguments!"
            );
        }
        LuaUtil.checkArg(
                "screen.setPixel",
                args, 0,
                Lua.LuaType.NUMBER
        );
        LuaUtil.checkArg(
                "screen.setPixel",
                args, 1,
                Lua.LuaType.NUMBER
        );
        LuaUtil.checkArg(
                "screen.setPixel",
                args, 2,
                Lua.LuaType.NUMBER
        );
        int x = Math.toIntExact(args[0].toInteger() & 0xFFFFFFFFL);
        int y = Math.toIntExact(args[1].toInteger() & 0xFFFFFFFFL);
        byte pixel = (byte) Math.toIntExact(args[2].toInteger() & 0xFF);

        LuaUtil.checkBounds("screen.setPixel", x, image.getWidth(), true, 0);
        LuaUtil.checkBounds("screen.setPixel", y, image.getHeight(), true, 1);

        image.setPixel(x - 1, y - 1, pixel);

        return LuaCCLib.EMPTY;
    }


}
