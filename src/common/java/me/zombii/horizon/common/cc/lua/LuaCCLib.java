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
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class LuaCCLib {

    public static final LuaValue[] EMPTY = new LuaValue[0];

    public static void inject(BlockEntityDevComputer computer) {
        Lua lua = computer.getLuaState();
        AddressableLuaEventBus internalBus = computer.getInternalPeripheralEventBus();

        lua.openLibraries();
        lua.pushNil();
        lua.setGlobal("java");

        lua.register("print", printFunction());

        lua.newTable();
        int t = lua.getTop();
        internalBus.push(lua);
        lua.setField(t, "eventBus");
        pushBios(lua, computer);
        lua.setField(t, "bios");

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
        LuaBiosChip chip = computer.getBios();
        if (chip == null) return;

        L.newTable();
        int t = L.getTop();

        String initCode = getInitScript(chip.getChip());
        L.load(initCode);
        L.setField(t, "init");

        chip.push(L);
        L.setField(t, "chip");
    }

}
