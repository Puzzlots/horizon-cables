package me.zombii.horizon.client;

import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.client.ClientModInit;
import finalforeach.cosmicreach.ui.UI;
import me.zombii.horizon.client.cc.screens.BiosFlasherScreen;
import me.zombii.horizon.client.screen.HorizonStyles;
import me.zombii.horizon.common.IHorizonClientBound;
import me.zombii.horizon.common.cc.blocks.BiosFlasherBlock;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class HorizonClient implements ClientModInit {

    @Override
    public void onClientInit() {
        HorizonStyles.init();
        HorizonClientRegistries.SCREEN_REGISTRY.store(
                BiosFlasherBlock.SCREEN_ID,
                BiosFlasherScreen::new
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
