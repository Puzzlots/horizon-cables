package me.zombii.horizon.client;

import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.client.ClientModInit;
import finalforeach.cosmicreach.ui.UI;
import me.zombii.horizon.client.cc.screens.ScreenBiosFlasher;
import me.zombii.horizon.client.cc.screens.ScreenDevComputer;
import me.zombii.horizon.client.screen.HorizonStyles;
import me.zombii.horizon.common.IHorizonClientBound;
import me.zombii.horizon.common.cc.blocks.bios.BlockBiosFlasher;
import me.zombii.horizon.common.cc.blocks.computer.BlockDevComputer;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class HorizonClient implements ClientModInit {

    @Override
    public void onClientInit() {
        HorizonStyles.init();
        HorizonClientRegistries.SCREEN_REGISTRY.store(
                BlockBiosFlasher.SCREEN_ID,
                ScreenBiosFlasher::new
        );
        HorizonClientRegistries.SCREEN_REGISTRY.store(
                BlockDevComputer.SCREEN_ID,
                ScreenDevComputer::new
        );

        IHorizonClientBound.INSTANCE.set(new IHorizonClientBound() {
            @Override
            public void openScreen(ScreenOpenInfo info) {
                UI.addOpenScreen(
                        () -> HorizonClientRegistries.SCREEN_REGISTRY.get(
                                info.screenId()
                        ).apply(info)
                );
            }
        });
    }

}
