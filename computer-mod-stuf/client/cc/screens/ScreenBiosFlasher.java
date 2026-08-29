package me.zombii.horizon.client.cc.screens;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import finalforeach.cosmicreach.items.ISlotContainerParent;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.ContainerSlotWidget;
import me.zombii.horizon.client.screen.CloseScreenIfTooFarAction;
import me.zombii.horizon.client.screen.HorizonBaseScreen;
import me.zombii.horizon.client.screen.HorizonStyles;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class ScreenBiosFlasher extends HorizonBaseScreen implements ISlotContainerParent {

    private final ContainerSlotWidget slotWidget;
    private final SlotContainer container;

    public ScreenBiosFlasher(ScreenOpenInfo info) {
        super(info.windowId(), info);

        BlockEntityBiosFlasher entity = (BlockEntityBiosFlasher) info.position().getBlockEntity();
        container = entity.getContainer();
        SlotContainerWindows.set(container, info.windowId());
        UI.openContainers.add(container);

        this.slotWidget = new ContainerSlotWidget(
                windowId,
                this,
                () -> container,
                0,
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch)
        );

        TextButton flashButton = new TextButton("Flash", HorizonStyles.textButtonStyle);
        flashButton.addAction(new Action() {
            boolean wasPressed = false;
            @Override
            public boolean act(float v) {
                if (!wasPressed && flashButton.isPressed()) {
                    wasPressed = true;
                }
                if (wasPressed && !flashButton.isPressed()) {
                    wasPressed = false;
                    entity.flashChip(info.player());
                }
                return false;
            }
        });

        Image background = new Image(HorizonStyles.background9Patch);
        background.setFillParent(true);
        Stack stack = new Stack();
        Table table = new Table();
        table.add(slotWidget).width(50).height(50).padRight(10);
        table.add(flashButton).width(100).height(50);

        stack.add(background);
        stack.add(table);
        mainActor = stack;
        mainActor.addAction(new CloseScreenIfTooFarAction(this, 7));
        init();
    }

    @Override
    public void onShow() {
        super.onShow();
        this.slotWidget.itemStackWidget.doDraw = true;
    }

    @Override
    public void onHide() {
        this.slotWidget.itemStackWidget.doDraw = false;
        this.closeRequested = true;
    }

    @Override
    public void onRemove() {
        super.onRemove();
//        container.getSlot(0).dropItems(windowId, getInfo().player());
        UI.openContainers.removeValue(container, true);
    }
}
