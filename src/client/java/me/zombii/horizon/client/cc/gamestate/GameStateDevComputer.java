package me.zombii.horizon.client.cc.gamestate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.IGameStateInWorld;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.ui.GameStyles;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.screens.ItemScreenComponent;
import finalforeach.cosmicreach.ui.widgets.ContainerSlotWidget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.zombii.horizon.client.cc.screens.CCScreenRenderer;
import me.zombii.horizon.client.screen.HorizonGameState;
import me.zombii.horizon.client.screen.HorizonStyles;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.screen.ScreenOpenInfo;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GameStateDevComputer extends HorizonGameState implements IGameStateInWorld {

    public GameStateDevComputer(ScreenOpenInfo info) {
        super(info);
    }

    private final ObjectList<ContainerSlotWidget> widgets = new ObjectArrayList<>();

    public void initInventory(Stack inventory) {
        Image inventoryBackground = new Image(HorizonStyles.background9Patch);
        inventoryBackground.setColor(0, 0, 1, 1);
        Table inventoryTable = new Table();
        inventoryTable.setFillParent(true);
        inventoryBackground.setFillParent(true);
        inventoryTable.center();
        inventory.add(inventoryBackground);
        inventory.add(inventoryTable);

        SlotContainer slotContainer = getInfo().player().inventory;

        for (int i = 10; i < slotContainer.numberOfSlots; i++) {
            ContainerSlotWidget slotWidget = new ContainerSlotWidget(
                    0, getInfo().player(), () -> slotContainer, i
            );
            widgets.add(slotWidget);
            inventoryTable.add(slotWidget).size(40);
            if (((i - 10) + 1) % 3 == 0) {
                inventoryTable.row();
            }
        }
    }

    private CCScreenRenderer renderer;
    private BlockEntityDevComputer entity;

    public static final GameStateDevComputer INSTANCE = new GameStateDevComputer(new ScreenOpenInfo(
            null, null, null, null, 0, true
    ));

    public HorizonGameState open(
            ScreenOpenInfo info
    ) {
        setInfo(info);
        setWindowId(info.windowId());
        entity = (BlockEntityDevComputer) getInfo().position().getBlockEntity();
        renderer = CCScreenRenderer.getOrNew(entity.screen);
        if (screenImage == null) {
            screenImage = new Image(new TextureRegionDrawable(region = new TextureRegion(region.getTexture())));
        } else {
            region.setRegion(renderer.getTexture());
        }
        return INSTANCE;
    }

    private Image screenImage;
    private TextureRegion region;

    private Image initScreen() {
        return screenImage;
    }

    @Override
    public void onSwitchTo() {
        super.onSwitchTo();

        UI.openContainers.add(getInfo().player().inventory);
        UI.openContainers.add(getInfo().player().cursor);

        SlotContainer container = entity.getContainer();
        SlotContainerWindows.set(container, getInfo().windowId());
        UI.openContainers.add(container);
        stage.getActors().add(InGame.IN_GAME.inGameUI.hotbarScreen.getActor());
        stage.getActors().add(UI.itemCursor);
    }

    @Override
    public void switchAwayTo(GameState gameState) {
        super.switchAwayTo(gameState);
        UI.openContainers.removeValue(getInfo().player().inventory, true);
        UI.openContainers.removeValue(getInfo().player().cursor, true);
        stage.getActors().removeValue(InGame.IN_GAME.inGameUI.hotbarScreen.getActor(), false);
        stage.getActors().removeValue(UI.itemCursor, false);
    }

    @Override
    public void create() {
        super.create();

        Image computerBackground = new Image(HorizonStyles.background9Patch);

        Stack inventory = new Stack();
        initInventory(inventory);

        float margin = 50;
        float width = newUiViewport.getWorldWidth() - (2 * margin);
        float height = newUiViewport.getWorldHeight() - (2 * margin);

        Table computerTable = new Table();
        computerTable.add(initScreen()).center().width(400).height(400);
        computerTable.row().height(height / 2);

        float x = margin;
        float y = margin;

        float margin2 = 5;

        inventory.setSize(
                (width / 5) - (margin2 * 2),
                height
        );
        inventory.setPosition(x, y);
        computerBackground.setSize(
                (width - (margin2 * 2)) - inventory.getWidth(),
                inventory.getHeight()
        );
        computerBackground.setPosition(
                newUiViewport.getWorldWidth() - margin - computerBackground.getWidth(),
                y
        );
        computerTable.setSize(computerBackground.getWidth(), computerBackground.getHeight());
        computerTable.setPosition(computerBackground.getX(), computerBackground.getY());

        stage.addActor(computerBackground);
        stage.addActor(inventory);
        stage.addActor(computerTable);
        stage.setDebugAll(false);
    }

    @Override
    public void render() {
        super.render();
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (ContainerSlotWidget widget : widgets) {
            widget.itemStackWidget.drawItem(ItemScreenComponent.itemViewport);
        }
        InGame.IN_GAME.inGameUI.hotbarScreen.drawItems();
        UI.itemCursor.itemStackWidget.drawItem(ItemScreenComponent.itemViewport);

        stage.getViewport().apply(true);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glCullFace(GL11.GL_FRONT);
        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);

        Batch stageBatch = stage.getBatch();
        stageBatch.begin();
        stageBatch.setColor(Color.WHITE);

        InGame.IN_GAME.inGameUI.hotbarScreen.drawItemCounts(stageBatch);
        for (ContainerSlotWidget widget : widgets) {
            widget.itemStackWidget.drawItemCountWithDropShadow(stageBatch, 0.0F, 0.0F, Color.DARK_GRAY);
        }

        for (ContainerSlotWidget widget : widgets) {
            widget.itemStackWidget.drawTooltip(stageBatch);
        }

        UI.itemCursor.itemStackWidget.drawItemCountWithDropShadow(stageBatch, 0.0F, 0.0F, Color.DARK_GRAY);
        stageBatch.end();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

}
