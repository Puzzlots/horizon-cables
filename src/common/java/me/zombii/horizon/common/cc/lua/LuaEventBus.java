package me.zombii.horizon.common.cc.lua;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class LuaEventBus {

    private final Object2ObjectOpenHashMap<String, List<LuaValue>> luaListeners = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, List<BiConsumer<String, String>>> javaListeners = new Object2ObjectOpenHashMap<>();

    public LuaEventBus() {}

    public LuaValue[] registerLua(Lua L, LuaValue[] args) {
        if (args.length != 2)
            L.error("eventbus.register must have 2 arguments.");
        if (!args[0].type().equals(Lua.LuaType.STRING))
            L.error("1st argument must be a string - eventbus.register");
        if (!args[1].type().equals(Lua.LuaType.FUNCTION))
            L.error("2nd argument must be a function - eventbus.register");

        String eventName = args[0].toString();
        LuaValue listener = args[1];

        System.out.println(eventName);

        List<LuaValue> list = luaListeners.getOrDefault(eventName, new ArrayList<>());
        list.add(listener);
        luaListeners.put(eventName, list);
        return LuaCCLib.EMPTY;
    }

    public void register(String eventName, BiConsumer<String, String> listener) {
        List<BiConsumer<String, String>> list = javaListeners.getOrDefault(eventName, new ArrayList<>());
        list.add(listener);
        javaListeners.put(eventName, list);
    }

    public LuaValue[] postLua(Lua L, LuaValue[] args) {
        if (args.length != 2)
            L.error("eventbus.post must have 2 arguments.");
        if (!args[0].type().equals(Lua.LuaType.STRING))
            L.error("1st argument must be a string - eventbus.post");
        if (!args[1].type().equals(Lua.LuaType.STRING))
            L.error("2nd argument must be a string - eventbus.post");

        post(args[0].toString(), args[1].toString());

        return LuaCCLib.EMPTY;
    }

    public void post(String eventName, String eventData) {
        List<LuaValue> listA = luaListeners.get(eventName);
        List<BiConsumer<String, String>> listB = javaListeners.get(eventName);

        if (listA != null && !listA.isEmpty()) {
            for (LuaValue listener : listA) {
                listener.call(eventName, eventData);
            }
        }
        if (listB != null && !listB.isEmpty()) {
            for (BiConsumer<String, String> listener : listB) {
                listener.accept(eventName, eventData);
            }
        }
    }

    public void push(Lua lua) {
        lua.newTable();
        int t = lua.getTop();
        lua.push(this::registerLua);
        lua.setField(t, "register");
        lua.push(this::postLua);
        lua.setField(t, "post");
    }

}
