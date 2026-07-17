package me.zombii.horizon.common.cc.lua.bus;

public interface LuaEventConsumer {

    void call(
            String fromAddress,
            String toAddress,
            String eventName,
            String eventDataStr
    );

}
