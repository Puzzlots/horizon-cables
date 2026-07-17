package me.zombii.horizon.client.screen;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class CloseScreenIfTooFarAction extends Action {

    private final HorizonBaseScreen screen;
    private final ScreenOpenInfo info;
    private final float dst2;

    public CloseScreenIfTooFarAction(HorizonBaseScreen screen, float distance) {
        this.screen = screen;
        this.info = screen.getInfo();
        this.dst2 = distance * distance;
    }

    @Override
    public boolean act(float v) {
        IReadBlockPosition blockPosition = info.position();
        Vector3 playerPosition = info.player().getPosition();

        if (playerPosition.dst2(
                blockPosition.getGlobalX(),
                blockPosition.getCenterY(),
                blockPosition.getGlobalZ()
        ) > dst2) {
            screen.closeRequested = true;
        }
        return false;
    }
}
