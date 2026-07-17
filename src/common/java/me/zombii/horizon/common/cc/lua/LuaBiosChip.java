package me.zombii.horizon.common.cc.lua;

import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

public class LuaBiosChip {

    private final BiosChip chip;

    public LuaBiosChip(BiosChip chip) {
        this.chip = chip;
    }

    public void push(Lua lua) {
        lua.newTable();
        int t = lua.getTop();
        lua.push(this::getSizeLua);
        lua.setField(t, "getSize");
        lua.push(this::getBytesLua);
        lua.setField(t, "getBytes");
        lua.push(this::readIntLua);
        lua.setField(t, "readInt");
        lua.push(this::readShortLua);
        lua.setField(t, "readShort");
        lua.push(this::readByteLua);
        lua.setField(t, "readByte");
    }

    public int getSize() {
        return chip.getMaxDiskSize();
    }

    public LuaValue[] getSizeLua(Lua L, LuaValue[] args) {
        L.push(getSize());
        return new LuaValue[]{L.get()};
    }

    public byte[] getBytes(int position, int size, byte[] buf) {
        byte[] data = chip.getData();
        System.arraycopy(data, position, buf, 0, size);
        return buf;
    }

    public byte[] getBytes(int position, int size) {
        return getBytes(position, size, new byte[size]);
    }

    public LuaValue[] getBytesLua(Lua L, LuaValue[] args) {
        if (args.length < 2)
            L.error("Too few arguments for 'chip.getBytes' min is 2");
        if (args.length > 3)
            L.error("Too many arguments for 'chip.getBytes' max is 3");

        if (args[0].type() != Lua.LuaType.NUMBER)
            L.error("'chip.getBytes' 1st argument must be a number");
        if (args[1].type() != Lua.LuaType.NUMBER)
            L.error("'chip.getBytes' 2nd argument must be a number");

        int position = Math.toIntExact(args[0].toInteger());
        int size = Math.toIntExact(args[1].toInteger());
        byte[] data = chip.getData();

        if (args.length == 3) {
            if (args[2].type() != Lua.LuaType.TABLE)
                L.error("'chip.getBytes' 3rd argument must be a table/array");

            L.push(args[2]);
            int t = L.getTop();
            for (int i = 0; i < size; i++) {
                L.push(data[position + i]);
                L.rawSetI(t, i + 1);
            }
            L.pop(1);
        } else {
            L.push(getBytes(position, size), Lua.Conversion.FULL);
            return new LuaValue[]{L.get()};
        }

        return new LuaValue[]{args[2]};
    }

    public int readInt(int position) {
        byte[] data = chip.getData();
        return (data[position] & 0xFF) << 24
                | (data[position + 1] & 0xFF) << 16
                | (data[position + 2] & 0xFF) << 8
                | (data[position + 3] & 0xFF);
    }

    public LuaValue[] readIntLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'chip.readInt' only takes in 1 argument");

        if (args[0].type() != Lua.LuaType.NUMBER)
            L.error("'chip.readInt' 1st argument must be a number");

        int position = Math.toIntExact(args[0].toInteger());

        L.push(readInt(position));
        return new LuaValue[]{L.get()};
    }

    public short readShort(int position) {
        byte[] data = chip.getData();
        return (short) ((data[position] & 0xFF) << 8
                        | (data[position + 1] & 0xFF));
    }

    public LuaValue[] readShortLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'chip.readShort' only takes in 1 argument");

        if (args[0].type() != Lua.LuaType.NUMBER)
            L.error("'chip.readShort' 1st argument must be a number");

        int position = Math.toIntExact(args[0].toInteger());

        L.push(readShort(position));
        return new LuaValue[]{L.get()};
    }

    public byte readByte(int position) {
        byte[] data = chip.getData();
        return data[position];
    }

    public LuaValue[] readByteLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'chip.readShort' only takes in 1 argument");

        if (args[0].type() != Lua.LuaType.NUMBER)
            L.error("'chip.readShort' 1st argument must be a number");

        int position = Math.toIntExact(args[0].toInteger());

        L.push(readByte(position));
        return new LuaValue[]{L.get()};
    }

    public BiosChip getChip() {
        return chip;
    }
}
