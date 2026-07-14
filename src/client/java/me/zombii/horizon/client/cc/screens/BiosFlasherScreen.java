package me.zombii.horizon.client.cc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import finalforeach.cosmicreach.items.containers.SlotContainerView;
import finalforeach.cosmicreach.ui.GameStyles;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.ItemStackWidget;
import me.zombii.horizon.client.screen.HorizonBaseScreen;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class BiosFlasherScreen extends HorizonBaseScreen {

    public BiosFlasherScreen(ScreenOpenInfo info) {
        super(-1, info);

        Stack stack = new Stack();
        mainActor = stack;

        init();
        Gdx.app.postRunnable(() -> {
            ItemStackWidget stackWidget = new ItemStackWidget(new NinePatchDrawable(GameStyles.container9Patch));
            stack.add(stackWidget);
        });
    }

}
