package me.zombii.horizon.common.cc.blocks.computer;

import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.containers.SlotContainer;

public class ContainerDevComputer extends SlotContainer {

    public ContainerDevComputer(int count) {
        super(count);
    }

    @Override
    protected ItemSlot createNewSlot(int slotNum) {
        return new ItemSlot(this, slotNum) {
            @Override
            public boolean allowedToInput(ItemStack itemStack) {
                if (itemStack == null) return true;
                return switch (slotNum) {
                    case 0 -> BlockEntityDevComputer.isValidBiosChip(itemStack);
                    case 1 -> BlockEntityDevComputer.isValidStorageMedia(itemStack);
                    case 2 -> BlockEntityDevComputer.isValidStorageMedia(itemStack);
                    default -> false;
                };

            }
        };
    }
}
