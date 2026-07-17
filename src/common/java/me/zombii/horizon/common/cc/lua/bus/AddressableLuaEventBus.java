package me.zombii.horizon.common.cc.lua.bus;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.zombii.horizon.common.cc.lua.LuaCCLib;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class AddressableLuaEventBus {

    private final Object2ObjectMap<String, List<LuaEventConsumer>> listeners = new Object2ObjectOpenHashMap<>();
    private final List<String> usedAddresses = new ObjectArrayList<>();
    private final List<String> freedAddresses = new ObjectArrayList<>();

    private static final Random random = new Random();

    public LuaValue[] isFreeLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'bus.isFreeLua' requires 1 argument!");
        if (!args[0].type().equals(Lua.LuaType.STRING))
            L.error("Argument #1 of 'bus.isFreeLua' must be a string, not '" + args[0].type() + "'");

        L.push(isFree(args[0].toString()));
        return new LuaValue[]{L.get()};
    }

    public boolean isFree(String yourAddress) {
        return freedAddresses.contains(yourAddress) || !usedAddresses.contains(yourAddress);
    }

    public LuaValue[] getNewAddressLua(Lua L, LuaValue[] value) {
        SmartEventBusHandle handle = getNewAddress();
        handle.push(L);
        return new LuaValue[]{L.get()};
    }

    public SmartEventBusHandle getNewAddress() {
        if (!freedAddresses.isEmpty()) {
            return new SmartEventBusHandle(this, freedAddresses.removeFirst());
        }

        String ipAddress = random.nextInt(0, 999) + "." + random.nextInt(0, 999);
        usedAddresses.add(ipAddress);

        return new SmartEventBusHandle(this, ipAddress);
    }

    public LuaValue[] freeAddressLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'bus.freeAddressLua' requires 1 argument!");
        if (!args[0].type().equals(Lua.LuaType.STRING))
            L.error("Argument #1 of 'bus.freeAddressLua' must be a string, not '" + args[0].type() + "'");

        freeAddress(args[0].toString());

        return LuaCCLib.EMPTY;
    }

    public void freeAddress(String address) {
        if (!freedAddresses.contains(address) && usedAddresses.contains(address)) {
            freedAddresses.add(address);
            usedAddresses.remove(address);
            unregisterAddress(address);
        }
    }

    public AddressableLuaEventBus() {}

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}");

    public LuaValue[] registerLua(Lua L, LuaValue[] args) {
        if (args.length != 2)
            L.error("eventbus.register must have 2 arguments.");
        if (!args[0].type().equals(Lua.LuaType.STRING))
            L.error("Argument #1 of 'eventbus.register' must be a string, not '" + args[0].type() + "'");
        if (!args[1].type().equals(Lua.LuaType.FUNCTION))
            L.error("Argument #2 of 'eventbus.register' must be a function, not '" + args[1].type() + "'");

        String yourAddress = args[0].toString();
        LuaValue listener = args[1];

        try {
            register(yourAddress, listener::call);
        } catch (IllegalArgumentException addressTypeException) {
            L.error(addressTypeException.getMessage());
        }
        return LuaCCLib.EMPTY;
    }

    public void register(String yourAddress, LuaEventConsumer listener) throws IllegalArgumentException {
        if (!ADDRESS_PATTERN.matcher(yourAddress).matches())
            throw new IllegalArgumentException("address must comply with the format 'xxx.xxx' with only 0-9 per x - eventbus.register");
        if (!usedAddresses.contains(yourAddress)) {
            throw new IllegalArgumentException("Tried to register to non-existent or freed address!");
        }

        List<LuaEventConsumer> list = listeners.getOrDefault(yourAddress, new ArrayList<>());
        list.add(listener);
        listeners.put(yourAddress, list);
    }

    public LuaValue[] postLua(Lua L, LuaValue[] args) {
        if (args.length != 4)
            L.error("eventbus.post must have 4 arguments.");
        for (int i = 0; i < 4; i++) {
            if (!args[i].type().equals(Lua.LuaType.STRING))
                L.error("Argument #" + (i + 1) + " must be a string, not '" + args[i].type() + "' - eventbus.post");
        }

        post(
                args[0].toString(),
                args[1].toString(),
                args[2].toString(),
                args[3].toString()
        );

        return LuaCCLib.EMPTY;
    }

    public void post(String fromAddress, String toAddress, String eventName, String eventData) throws IllegalArgumentException {
        if (!ADDRESS_PATTERN.matcher(fromAddress).matches())
            throw new IllegalArgumentException("address must comply with the format 'xxx.xxx' with only 0-9 per x - eventbus.register");
        if (!usedAddresses.contains(fromAddress)) {
            throw new IllegalArgumentException("Tried to register to non-existent or freed address!");
        }

        if (toAddress.equals("*")) {
            for (Map.Entry<String, List<LuaEventConsumer>> addressListenerPair : listeners.entrySet()) {
                for (LuaEventConsumer luaEventConsumer : addressListenerPair.getValue()) {
                    luaEventConsumer.call(
                            fromAddress,
                            addressListenerPair.getKey(),
                            eventName,
                            eventData
                    );
                }
            }
        } else {
            List<LuaEventConsumer> list = listeners.get(toAddress);
            if (list != null) {
                for (LuaEventConsumer luaEventConsumer : list) {
                    luaEventConsumer.call(
                            fromAddress,
                            toAddress,
                            eventName,
                            eventData
                    );
                }
            }
        }
    }

    public void unregisterAddress(String address) {
        listeners.remove(address);
    }

    public LuaValue[] unregisterLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("eventbus.register must have 1 argument.");
        if (!args[0].type().equals(Lua.LuaType.STRING))
            L.error("1st argument must be a address string - eventbus.unregister");

        String address = args[0].toString();
        unregisterAddress(address);
        return LuaCCLib.EMPTY;
    }

    public void push(Lua lua) {
        lua.newTable();
        int t = lua.getTop();

        lua.push(this::registerLua);
        lua.setField(t, "register");

        lua.push(this::postLua);
        lua.setField(t, "post");

        lua.push(this::unregisterLua);
        lua.setField(t, "unregister");

        lua.push(this::freeAddressLua);
        lua.setField(t, "freeAddress");

        lua.push(this::getNewAddressLua);
        lua.setField(t, "getNewAddress");

        lua.push(this::isFreeLua);
        lua.setField(t, "isFree");
    }

    public void reset() {
        usedAddresses.clear();
        freedAddresses.clear();
        listeners.clear();
    }
}
