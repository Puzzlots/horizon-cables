package me.zombii.horizon.client.screen;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import finalforeach.cosmicreach.ui.GameStyles;
import finalforeach.cosmicreach.util.assets.GameTexture;

public class HorizonStyles {
    public static NinePatch background9Patch;
    public static NinePatch slotPatch;
    public static NinePatch buttonPatch;
    public static NinePatch buttonHoverPatch;
    public static NinePatch buttonPressPatch;
    public static Button.ButtonStyle buttonStyle;
    public static TextButton.TextButtonStyle textButtonStyle;

    public static void init() {
        background9Patch = new NinePatch(GameTexture.load("horizon:textures/ui/transparent-monochrome-background.png").get(), 9, 9, 9, 9);
        slotPatch = new NinePatch(GameTexture.load("horizon:textures/ui/rounded-item-slot.png").get(), 9, 9, 9, 9);
        buttonPatch = new NinePatch(GameTexture.load("horizon:textures/ui/button-nine-patch.png").get(), 9, 9, 9, 9);
        buttonHoverPatch = new NinePatch(GameTexture.load("horizon:textures/ui/button-hover-nine-patch.png").get(), 9, 9, 9, 9);
        buttonPressPatch = new NinePatch(GameTexture.load("horizon:textures/ui/button-pressed-nine-patch.png").get(), 9, 9, 9, 9);

        buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = new NinePatchDrawable(buttonPatch);
        buttonStyle.over = new NinePatchDrawable(buttonHoverPatch);
        buttonStyle.down = new NinePatchDrawable(buttonPressPatch);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = buttonStyle.up;
        textButtonStyle.over = buttonStyle.over;
        textButtonStyle.down = buttonStyle.down;
        textButtonStyle.font = GameStyles.textstyle.font;
    }
}
