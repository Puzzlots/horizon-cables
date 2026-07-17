package me.zombii.horizon.client.screen;

import finalforeach.cosmicreach.ui.screens.BaseScreen;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class HorizonBaseScreen extends BaseScreen {

    private final ScreenOpenInfo info;

    public HorizonBaseScreen(
            int windowId,
            ScreenOpenInfo info
    ) {
        super(windowId);
        this.info = info;
    }

    public ScreenOpenInfo getInfo() {
        return info;
    }
}
