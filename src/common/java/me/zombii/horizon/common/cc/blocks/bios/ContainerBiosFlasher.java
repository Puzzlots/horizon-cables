package me.zombii.horizon.common.cc.blocks.bios;

import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.containers.SlotContainer;

public class ContainerBiosFlasher extends SlotContainer {

    public ContainerBiosFlasher(int count) {
        super(count);
    }

    @Override
    protected ItemSlot createNewSlot(int slotNum) {
        return new ItemSlot(this, slotNum) {
            @Override
            public boolean allowedToInput(ItemStack itemStack) {
                return itemStack == null || BlockEntityBiosFlasher.isValid(itemStack);
            }
        };
    }
}
