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

    public static NinePatch powerButtonOnUpPatch;
    public static NinePatch powerButtonOnHoverPatch;
    public static NinePatch powerButtonOnPressPatch;

    public static NinePatch powerButtonOffUpPatch;
    public static NinePatch powerButtonOffHoverPatch;
    public static NinePatch powerButtonOffPressPatch;

    public static Button.ButtonStyle buttonStyle;
    public static TextButton.TextButtonStyle textButtonStyle;

    public static void init() {
        background9Patch = new NinePatch(GameTexture.load("horizon:textures/ui/transparent-monochrome-background.png").get(), 9, 9, 9, 9);
        slotPatch = newNinePatch("horizon:textures/ui/rounded-item-slot.png");
        buttonPatch = newNinePatch("horizon:textures/ui/button-nine-patch.png");
        buttonHoverPatch = newNinePatch("horizon:textures/ui/button-hover-nine-patch.png");
        buttonPressPatch = newNinePatch("horizon:textures/ui/button-pressed-nine-patch.png");

        powerButtonOnUpPatch = newNinePatch("horizon:textures/ui/power-button-on.png");
        powerButtonOnHoverPatch = newNinePatch("horizon:textures/ui/power-button-on-hover.png");
        powerButtonOnPressPatch = newNinePatch("horizon:textures/ui/power-button-on-pressed.png");

        powerButtonOffUpPatch = newNinePatch("horizon:textures/ui/power-button-off.png");
        powerButtonOffHoverPatch = newNinePatch("horizon:textures/ui/power-button-off-hover.png");
        powerButtonOffPressPatch = newNinePatch("horizon:textures/ui/power-button-off-pressed.png");

        buttonStyle = newStyle(buttonPatch, buttonHoverPatch, buttonPressPatch);
        textButtonStyle = newStyle(buttonStyle);

        powerButtonOnStyle = newStyle(powerButtonOnUpPatch, powerButtonOnHoverPatch, powerButtonOnPressPatch);
        powerTextButtonOnStyle = newStyle(powerButtonOnStyle);

        powerButtonOffStyle = newStyle(powerButtonOffUpPatch, powerButtonOffHoverPatch, powerButtonOffPressPatch);
        powerTextButtonOffStyle = newStyle(powerButtonOffStyle);
    }

    private static NinePatch newNinePatch(String location) {
        return  new NinePatch(GameTexture.load(location).get(), 9, 9, 9, 9);
    }

    private static Button.ButtonStyle newStyle(
            NinePatch up,
            NinePatch hover,
            NinePatch press
    ) {
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = new NinePatchDrawable(up);
        buttonStyle.over = new NinePatchDrawable(hover);
        buttonStyle.down = new NinePatchDrawable(press);
        return buttonStyle;
    }

    private static TextButton.TextButtonStyle newStyle(Button.ButtonStyle style) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = GameStyles.textstyle.font;
        textButtonStyle.up = style.up;
        textButtonStyle.over = style.over;
        textButtonStyle.down = style.down;
        return textButtonStyle;
    }
}
