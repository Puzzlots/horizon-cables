package me.zombii.horizon.common.cc.lua.bus;

import me.zombii.horizon.common.cc.lua.LuaCCLib;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

public class SmartEventBusHandle {

    private final String yourAddress;
    private final AddressableLuaEventBus eventBus;

    protected SmartEventBusHandle(
            AddressableLuaEventBus eventBus,
            String yourAddress
    ) {
        this.eventBus = eventBus;
        this.yourAddress = yourAddress;
    }

    public LuaValue[] postEventLua(Lua L, LuaValue[] args) {
        if (args.length != 3)
            L.error("'handle.postEvent' requires 3 arguments!");
        for (int i = 0; i < args.length; i++) {
            if (!args[i].type().equals(Lua.LuaType.STRING))
                L.error("Argument #" + (i + 1) + " of 'handle.postEvent' must be a string, not '" + args[i].type() + "' - eventbus.post");
        }

        String toAddress = args[0].toString();
        String eventName = args[1].toString();
        String eventData = args[2].toString();

        try {
            postEvent(toAddress, eventName, eventData);
        } catch (IllegalStateException | IllegalArgumentException e) {
            L.error(e.getMessage());
        }

        return LuaCCLib.EMPTY;
    }

    public void postEvent(String toAddress, String eventName, String eventData) throws IllegalStateException, IllegalArgumentException {
        if (isFree()) throw new IllegalStateException("Cannot post events using a freed event bus handle!");
        eventBus.post(yourAddress, toAddress, eventName, eventData);
    }

    public LuaValue[] registerEventHandlerLua(Lua L, LuaValue[] args) {
        if (args.length != 1)
            L.error("'handle.registerEventHandler' requires 1 argument!");
        if (!args[0].type().equals(Lua.LuaType.FUNCTION))
            L.error("Argument #1 of 'handle.registerEventHandler' must be a function, not '" + args[0].type() + "'");

        try {
            registerEventHandler(args[0]::call);
        } catch (IllegalStateException | IllegalArgumentException e) {
            L.error(e.getMessage());
        }

        return LuaCCLib.EMPTY;
    }

    public void registerEventHandler(LuaEventConsumer listener) throws IllegalStateException, IllegalArgumentException {
        if (isFree()) throw new IllegalStateException("Cannot register event handlers using a freed event bus handle!");
        eventBus.register(yourAddress, listener);
    }

    public LuaValue[] unregisterHandlersLua(Lua L, LuaValue[] args) {
        unregisterHandlers();
        return LuaCCLib.EMPTY;
    }

    public void unregisterHandlers() {
        if (isFree()) throw new IllegalStateException("Cannot unregister handlers using a freed event bus handle!");
        eventBus.unregisterAddress(yourAddress);
    }

    public LuaValue[] freeHandleLua(Lua L, LuaValue[] args) {
        freeHandle();
        return LuaCCLib.EMPTY;
    }

    public void freeHandle() {
        eventBus.freeAddress(yourAddress);
    }

    public LuaValue[] getAddressLua(Lua L, LuaValue[] args) {
        L.push(getAddress());
        return new LuaValue[]{L.get()};
    }

    public String getAddress() {
        return yourAddress;
    }

    public LuaValue[] isFreeLua(Lua L, LuaValue[] args) {
        L.push(isFree());
        return new LuaValue[]{L.get()};
    }

    public boolean isFree() {
        return eventBus.isFree(yourAddress);
    }

    public void push(Lua L) {
        L.newTable();
        int t = L.getTop();

        L.push(this::postEventLua);
        L.setField(t, "postEvent");

        L.push(this::registerEventHandlerLua);
        L.setField(t, "registerEventHandler");

        L.push(this::unregisterHandlersLua);
        L.setField(t, "unregisterHandlers");

        L.push(this::freeHandleLua);
        L.setField(t, "freeHandle");

        L.push(this::getAddressLua);
        L.setField(t, "getAddress");

        L.push(this::isFreeLua);
        L.setField(t, "isFree");
    }

}
