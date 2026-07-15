package me.zombii.horizon.common.cc.blocks;

import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import me.zombii.horizon.common.HorizonTags;

public class BiosFlasherContainer extends SlotContainer {

    public BiosFlasherContainer(int count) {
        super(count);
    }

    @Override
    protected ItemSlot createNewSlot(int slotNum) {
        return new ItemSlot(this, slotNum) {
            @Override
            public boolean allowedToInput(ItemStack itemStack) {
                return BiosFlasherBlockEntity.isValid(itemStack);
            }
        };
    }
}
