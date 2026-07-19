package me.zombii.horizon.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.ScreenUtils;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.IGameStateInWorld;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.settings.Controls;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.ui.InGameUI;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.screens.BaseScreen;
import me.zombii.horizon.common.screen.ScreenOpenInfo;
import org.lwjgl.opengl.GL11;

public class HorizonGameState extends GameState implements IGameStateInWorld {

    private ScreenOpenInfo info;
    private int windowId;

    public HorizonGameState(
            ScreenOpenInfo info
    ) {
        super();
        this.windowId = info.windowId();
        this.info = info;
    }

    public void setInfo(ScreenOpenInfo info) {
        this.info = info;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    public void onSwitchTo() {
        super.onSwitchTo();
        Gdx.input.setInputProcessor(this.stage);
        Gdx.input.setCursorCatched(false);
        batch.setProjectionMatrix(newUiViewport.getCamera().combined);
    }

    @Override
    public void switchAwayTo(GameState gameState) {
        super.switchAwayTo(gameState);
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCursorCatched(true);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        InGame.IN_GAME.update(deltaTime);
    }

    @Override
    public void render() {
        super.render();
        InGame.IN_GAME.render();
        stage.act();

        if (Controls.pauseJustPressed())
            GameState.switchToGameState(GameState.IN_GAME);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glCullFace(GL11.GL_FRONT);

        stage.draw();
    }

    public ScreenOpenInfo getInfo() {
        return info;
    }
}
