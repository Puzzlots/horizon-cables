package me.zombii.horizon.client.cc.screens;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import finalforeach.cosmicreach.items.ISlotContainerParent;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.ContainerSlotWidget;
import me.zombii.horizon.client.screen.CloseScreenIfTooFarAction;
import me.zombii.horizon.client.screen.HorizonBaseScreen;
import me.zombii.horizon.client.screen.HorizonStyles;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class ScreenDevComputer extends HorizonBaseScreen implements ISlotContainerParent {

    private final ContainerSlotWidget biosChipSlotWidget;
    private final ContainerSlotWidget nonportableStorageSlotWidget;
    private final ContainerSlotWidget portableStorageSlotWidget;
    private final SlotContainer container;

    public ScreenDevComputer(ScreenOpenInfo info) {
        super(info.windowId(), info);

        BlockEntityDevComputer entity = (BlockEntityDevComputer) info.position().getBlockEntity();
        container = entity.getContainer();
        SlotContainerWindows.set(container, info.windowId());
        UI.openContainers.add(container);

        this.biosChipSlotWidget = new ContainerSlotWidget(
                windowId,
                this,
                () -> container,
                0,
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch)
        );

        this.nonportableStorageSlotWidget = new ContainerSlotWidget(
                windowId,
                this,
                () -> container,
                1,
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch)
        );

        this.portableStorageSlotWidget = new ContainerSlotWidget(
                windowId,
                this,
                () -> container,
                2,
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch),
                new NinePatchDrawable(HorizonStyles.slotPatch)
        );

        Button onButton = new Button(
                entity.getPowerState() ?
                HorizonStyles.powerButtonOnStyle :
                HorizonStyles.powerButtonOffStyle
        );
        onButton.addAction(new Action() {
            boolean wasPressed = false;

            @Override
            public boolean act(float v) {
                if (!wasPressed && onButton.isPressed()) {
                    wasPressed = true;
                }
                if (wasPressed && !onButton.isPressed()) {
                    wasPressed = false;
                    entity.setPowerState(!entity.getPowerState());
                    onButton.setStyle(
                            entity.getPowerState() ?
                            HorizonStyles.powerButtonOnStyle :
                            HorizonStyles.powerButtonOffStyle
                    );
//                    onButton.setText(entity.getPowerState() ? "turn off" : "turn on");
                }
                return false;
            }
        });

        Image background = new Image(HorizonStyles.background9Patch);
        background.setFillParent(true);
        Stack stack = new Stack();
        Table table = new Table();
        table.add(biosChipSlotWidget).width(50).height(50).padRight(10).right();
        table.add(nonportableStorageSlotWidget).width(50).height(50).padRight(10).right();
        table.add(portableStorageSlotWidget).width(50).height(50).padRight(10).right();

        table.add(onButton).width(50).height(50).padRight(10).left();

        stack.add(background);
        stack.add(table);
        mainActor = stack;
        mainActor.addAction(new CloseScreenIfTooFarAction(this, 7));
        init();
    }

    @Override
    public void onShow() {
        super.onShow();
        this.biosChipSlotWidget.itemStackWidget.doDraw = true;
        this.nonportableStorageSlotWidget.itemStackWidget.doDraw = true;
        this.portableStorageSlotWidget.itemStackWidget.doDraw = true;
    }

    @Override
    public void onHide() {
        this.biosChipSlotWidget.itemStackWidget.doDraw = false;
        this.nonportableStorageSlotWidget.itemStackWidget.doDraw = false;
        this.portableStorageSlotWidget.itemStackWidget.doDraw = false;
        this.closeRequested = true;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        UI.openContainers.removeValue(container, true);
    }

}
