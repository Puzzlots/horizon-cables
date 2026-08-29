package me.zombii.horizon.common.cc.lua;

import org.apache.commons.lang3.function.TriFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LuaImageApi {

    static {
        ImageIO.scanForPlugins();
    }

    public static void push(Lua L) {
        L.newTable();
        int t = L.getTop();

        pushFunction(L, t, LuaImageApi::fromBytes, "fromBytes");
    }

    private static void pushFunction(
            Lua lua,
            int idx,
            LuaFunction f,
            String name
    ) {
        lua.push(f);
        lua.setField(idx, name);
    }

    private static void pushFunction(
            Lua lua,
            int idx,
            BufferedImage img,
            TriFunction<BufferedImage, Lua, LuaValue[], LuaValue[]> f,
            String name
    ) {
        lua.push((L, args) -> f.apply(img, L, args));
        lua.setField(idx, name);
    }

    public static LuaValue[] fromBytes(Lua L, LuaValue[] args) {
        if (args.length != 1) {
            throw new LuaException(
                    LuaException.LuaError.RUNTIME,
                    "'imageio.fromBytes' requires 1 argument!"
            );
        }
        LuaUtil.checkArg(
                "imageio.fromBytes",
                args, 0,
                Lua.LuaType.TABLE
        );
        LuaValue array = args[0];
        // do checking to ensure it is a byte array
        for (LuaValue luaValue : array.keySet()) {
            if (luaValue.type() != Lua.LuaType.NUMBER) {
                throw new LuaException(
                        LuaException.LuaError.RUNTIME,
                        "'imageio.fromBytes' expects an array/table with only number indices, found index '" + luaValue + "' instead."
                );
            }
            LuaValue v = array.get(luaValue);
            if (v.type() != Lua.LuaType.NUMBER) {
                throw new LuaException(
                        LuaException.LuaError.RUNTIME,
                        "'imageio.fromBytes' expects an array of bytes. Found a '" + v.type() + "' at '" + luaValue + "'."
                );
            }
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (int i = 0; i < array.length(); i++) {
                outputStream.write(Math.toIntExact(array.get(i + 1).toInteger()) & 0xFF);
            }
            outputStream.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();

            wrap(L, image);

        } catch (IOException e) {
            throw new LuaException(LuaException.LuaError.RUNTIME, e.getMessage());
        }


        return new LuaValue[]{L.get()};
    }

    private static void wrap(Lua L, BufferedImage image) {
        L.newTable();
        int t = L.getTop();

        pushFunction(L, t, LuaUtil.newNumberGetter(image::getWidth), "getWidth");
        pushFunction(L, t, LuaUtil.newNumberGetter(image::getHeight), "getHeight");
        pushFunction(L, t, image, LuaImageApi::setPixel, "setPixel");
        pushFunction(L, t, image, LuaImageApi::getPixel, "getPixel");
    }

    public static LuaValue[] getPixel(BufferedImage image, Lua L, LuaValue[] args) {
        if (args.length != 2) {
            throw new LuaException(
                    LuaException.LuaError.RUNTIME,
                    "'imageio.getPixel' requires 2 arguments!"
            );
        }
        LuaUtil.checkArg(
                "imageio.getPixel",
                args, 0,
                Lua.LuaType.NUMBER
        );
        LuaUtil.checkArg(
                "imageio.getPixel",
                args, 1,
                Lua.LuaType.NUMBER
        );
        int x = Math.toIntExact(args[0].toInteger() & 0xFFFFFFFFL);
        int y = Math.toIntExact(args[1].toInteger() & 0xFFFFFFFFL);
        LuaUtil.checkBounds("imageio.getPixel", x, image.getWidth(), true, 0);
        LuaUtil.checkBounds("imageio.getPixel", y, image.getHeight(), true, 1);

        L.push(image.getRGB(x - 1, y - 1));

        return new LuaValue[]{L.get()};
    }

    public static LuaValue[] setPixel(BufferedImage image, Lua L, LuaValue[] args) {
        if (args.length != 3) {
            throw new LuaException(
                    LuaException.LuaError.RUNTIME,
                    "'imageio.setPixel' requires 3 arguments!"
            );
        }
        LuaUtil.checkArg(
                "imageio.setPixel",
                args, 0,
                Lua.LuaType.NUMBER
        );
        LuaUtil.checkArg(
                "imageio.setPixel",
                args, 1,
                Lua.LuaType.NUMBER
        );
        LuaUtil.checkArg(
                "imageio.setPixel",
                args, 2,
                Lua.LuaType.NUMBER
        );
        int x = Math.toIntExact(args[0].toInteger() & 0xFFFFFFFFL);
        int y = Math.toIntExact(args[1].toInteger() & 0xFFFFFFFFL);
        int pixel = Math.toIntExact(args[2].toInteger() & 0xFFFFFFFFL);

        LuaUtil.checkBounds("imageio.setPixel", x, image.getWidth(), true, 0);
        LuaUtil.checkBounds("imageio.setPixel", y, image.getHeight(), true, 1);

        image.setRGB(x - 1, y - 1, pixel);

        return LuaCCLib.EMPTY;
    }

}
