package me.zombii.horizon.common.screen;

import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletons;
import me.zombii.horizon.common.IHorizonClientBound;

public class ScreenManager {

    public static void openScreen(ScreenOpenInfo screenOpenInfo) {
        if (GameSingletons.isClient()) {
            IHorizonClientBound.INSTANCE.get().openScreen(screenOpenInfo);
            return;
        }
        ServerSingletons.getConnection(screenOpenInfo.player())
                .send(new PacketOpenScreen(screenOpenInfo));
    }

}
