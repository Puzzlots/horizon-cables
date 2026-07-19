package me.zombii.horizon.client.cc.gamestate;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.IGameStateInWorld;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.ItemSlotInteractions;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.ContainerSlotWidget;
import me.zombii.horizon.client.screen.HorizonGameState;
import me.zombii.horizon.client.screen.HorizonStyles;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class GameStateDevComputer extends HorizonGameState implements IGameStateInWorld {

    public GameStateDevComputer(ScreenOpenInfo info) {
        super(info);
    }

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
            slotWidget.itemStackWidget.doDraw = true;
            inventoryTable.add(slotWidget).size(50);
            if (((i - 10) + 1) % 7 == 0) {
                inventoryTable.row();
            }
        }
        for (int i = 0; i < 10; i++) {
            ContainerSlotWidget slotWidget = new ContainerSlotWidget(
                    0, getInfo().player(), () -> slotContainer, i
            );
            slotWidget.itemStackWidget.doDraw = true;
            inventoryTable.add(slotWidget).size(50);
            if (((slotContainer.numberOfSlots + i - 10) + 1) % 7 == 0) {
                inventoryTable.row();
            }
        }

    }

    @Override
    public void onSwitchTo() {
        super.onSwitchTo();
        UI.setInventoryOpen(true);
        UI.openContainers.add(getInfo().player().inventory);
        UI.openContainers.add(getInfo().player().cursor);

        BlockEntityDevComputer entity = (BlockEntityDevComputer) getInfo().position().getBlockEntity();
        SlotContainer container = entity.getContainer();
        SlotContainerWindows.set(container, getInfo().windowId());
        UI.openContainers.add(container);
    }

    @Override
    public void switchAwayTo(GameState gameState) {
        super.switchAwayTo(gameState);
        UI.setInventoryOpen(false);
        UI.canDropItemCursorOnClick = false;
        UI.openContainers.removeValue(getInfo().player().inventory, true);
        UI.openContainers.removeValue(getInfo().player().cursor, true);
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

        float x = margin;
        float y = margin;

        float margin2 = 5;

        inventory.setSize(
                (width / 2) - (margin2 * 2),
                height
        );
        inventory.setPosition(x, y);
        computerBackground.setSize(
                inventory.getWidth(),
                inventory.getHeight()
        );
        computerBackground.setPosition(newUiViewport.getWorldWidth() - margin - inventory.getWidth(), y);

        stage.addActor(computerBackground);
        stage.addActor(inventory);
        stage.setDebugAll(true);
    }

    @Override
    public void render() {
        super.render();
        UI.canDropItemCursorOnClick = false;
    }
}
