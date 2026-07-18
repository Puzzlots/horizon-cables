package me.zombii.horizon.common.cc.lua;

import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import org.apache.commons.lang3.function.TriFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

public class LuaStorageApi {

    public static void push(Lua L, AbstractDataStorageDevice d, boolean readOnly) {
        L.newTable();
        int t = L.getTop();

        pushFunction(L, t, d, LuaStorageApi::readByteLua, "readByte");
        pushFunction(L, t, d, LuaStorageApi::readShortLua, "readShort");
        pushFunction(L, t, d, LuaStorageApi::readIntLua, "readInt");
        pushFunction(L, t, d, LuaStorageApi::getBytesLua, "getBytes");
        pushFunction(L, t, d, LuaStorageApi::getSizeLua, "getSize");

        if (!readOnly) {
            pushFunction(L, t, d, LuaStorageApi::writeByte, "writeByte");
            pushFunction(L, t, d, LuaStorageApi::writeShort, "writeShort");
            pushFunction(L, t, d, LuaStorageApi::writeInt, "writeInt");
            pushFunction(L, t, d, LuaStorageApi::writeBytes, "writeBytes");
        }
    }

    private static void pushFunction(
            Lua lua,
            int idx,
            AbstractDataStorageDevice device,
            TriFunction<AbstractDataStorageDevice, Lua, LuaValue[], LuaValue[]> f,
            String name
    ) {
        lua.push((L, args) -> f.apply(device, L, args));
        lua.setField(idx, name);
    }

    public static LuaValue[] getSizeLua(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        L.push(device.getMaxDiskSize());
        return new LuaValue[]{L.get()};
    }

    public static LuaValue[] getBytesLua(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length < 2 || args.length > 3)
            L.error("'storage.getBytes' requires 2 or 3 arguments, not " + args.length);

        LuaUtil.checkArg("storage.getBytes", args, 0, Lua.LuaType.NUMBER);
        LuaUtil.checkArg("storage.getBytes", args, 1, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());
        int size = Math.toIntExact(args[1].toInteger());
        byte[] data = device.getData();

        if (args.length == 3) {
            LuaUtil.checkArg("storage.getBytes", args, 2, Lua.LuaType.TABLE);

            L.push(args[2]);
            int t = L.getTop();
            for (int i = 0; i < size; i++) {
                L.push(data[position + i]);
                L.rawSetI(t, i + 1);
            }
            L.pop(1);
        } else {
            L.push(device.getBytes(position, size), Lua.Conversion.FULL);
            return new LuaValue[]{L.get()};
        }

        return new LuaValue[]{args[2]};
    }

    public static LuaValue[] readIntLua(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'storage.readInt' requires 1 argument!");

        LuaUtil.checkArg("storage.readInt", args, 0, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());

        L.push(device.readInt(position));
        return new LuaValue[]{L.get()};
    }

    public static LuaValue[] readShortLua(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'storage.readShort' requires 1 argument!");

        LuaUtil.checkArg("storage.readShort", args, 0, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());

        L.push(device.readShort(position));
        return new LuaValue[]{L.get()};
    }

    public static LuaValue[] readByteLua(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'storage.readByte' requires 1 argument!");

        LuaUtil.checkArg("storage.readByte", args, 0, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());

        L.push(device.readByte(position));
        return new LuaValue[]{L.get()};
    }

    public static LuaValue[] writeByte(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 2)
            L.error("'storage.writeByte' requires 2 arguments");

        LuaUtil.checkArg("storage.writeByte", args, 0, Lua.LuaType.NUMBER);
        LuaUtil.checkArg("storage.writeByte", args, 1, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());
        byte value = (byte) (Math.toIntExact(args[1].toInteger()) & 0xFF);
        device.writeByte(position, value);

        return LuaCCLib.EMPTY;
    }

    public static LuaValue[] writeShort(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 2)
            L.error("'storage.writeShort' requires 2 arguments");

        LuaUtil.checkArg("storage.writeShort", args, 0, Lua.LuaType.NUMBER);
        LuaUtil.checkArg("storage.writeShort", args, 1, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());
        short value = (short) (Math.toIntExact(args[1].toInteger()) & 0xFFFF);
        device.writeShort(position, value);

        return LuaCCLib.EMPTY;
    }

    public static LuaValue[] writeInt(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 2)
            L.error("'storage.writeInt' requires 2 arguments");

        LuaUtil.checkArg("storage.writeInt", args, 0, Lua.LuaType.NUMBER);
        LuaUtil.checkArg("storage.writeInt", args, 1, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());
        int value = (int) (Math.toIntExact(args[1].toInteger()) & 0xFFFFFFFFL);
        device.writeInt(position, value);

        return LuaCCLib.EMPTY;
    }

    public static LuaValue[] writeBytes(AbstractDataStorageDevice device, Lua L, LuaValue[] args) {
        if (args.length != 4)
            L.error("'storage.writeBytes' requires 2 arguments");

        LuaUtil.checkArg("storage.writeBytes", args, 0, Lua.LuaType.NUMBER);
        LuaUtil.checkArg("storage.writeBytes", args, 1, Lua.LuaType.TABLE);
        LuaUtil.checkArg("storage.writeBytes", args, 2, Lua.LuaType.NUMBER);
        LuaUtil.checkArg("storage.writeBytes", args, 3, Lua.LuaType.NUMBER);

        int position = Math.toIntExact(args[0].toInteger());
        int offset = Math.toIntExact(args[2].toInteger());
        int length = Math.toIntExact(args[3].toInteger());

        for (int i = 0; i < length; i++) {
            device.writeByte(position + i, (byte) (Math.toIntExact(args[1].get(1 + i + offset).toInteger()) & 0xFF));
        }

        return LuaCCLib.EMPTY;
    }

}
