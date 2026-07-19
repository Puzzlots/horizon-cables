package me.zombii.horizon.common.cc.lua;

import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.packets.MessagePacket;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletons;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.lua.bus.AddressableLuaEventBus;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class LuaCCLib {

    public static final LuaValue[] EMPTY = new LuaValue[0];

    public static Lua newLua() {
        return new LuaJit();
    }

    private static void removeGlobal(Lua L, String name) {
        L.pushNil();
        L.setGlobal(name);
    }

    public static void inject(BlockEntityDevComputer computer) {
        Lua lua = computer.getLuaState();
        AddressableLuaEventBus internalBus = computer.getInternalBus();

        lua.openLibraries();
        removeGlobal(lua, "java");
        removeGlobal(lua, "io");
        removeGlobal(lua, "ffi");
        removeGlobal(lua, "jit");
        removeGlobal(lua, "dofile");
        removeGlobal(lua, "loadfile");
        removeGlobal(lua, "require");

        lua.register("print", printFunction());

        lua.newTable();
        int t = lua.getTop();
        internalBus.push(lua);
        lua.setField(t, "eventBus");
        pushBios(lua, computer);
        lua.setField(t, "bios");
        LuaPeripheralApi.push(lua, computer);
        lua.setField(t, "peripherals");
        LuaImageApi.push(lua);
        lua.setField(t, "imageio");

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
//            buf.append("\n");
//            System.out.print(buf);
            if (GameSingletons.isClient()) {
                Chat.MAIN_CLIENT_CHAT.addMessage(null, buf.toString());
            } else {
                for (NetworkIdentity allNetId : ServerSingletons.getAllNetIds()) {
                    allNetId.send(new MessagePacket(buf.toString()));
                }
            }
            return EMPTY;
        };
    }

    private static String getInitScript(BiosChip chip) {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(chip.getData()));
        try {
            int byteCount = stream.readInt();
            byte[] data = new byte[byteCount];
            stream.read(data);
            stream.close();

            return new String(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void pushBios(Lua L, BlockEntityDevComputer computer) {
        BiosChip chip = computer.getBios();
        if (chip == null) return;

        L.newTable();
        int t = L.getTop();

        String initCode = getInitScript(chip);
        L.load(initCode);
        L.setField(t, "init");

        LuaStorageApi.push(L, chip, true);
        L.setField(t, "chip");
    }

}
