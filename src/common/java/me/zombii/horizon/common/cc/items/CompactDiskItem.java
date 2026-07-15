package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class CompactDiskItem extends AbstractDataStorageItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "compact-disk");

    public CompactDiskItem() {
        super(ID);
        addTexture(ItemModelType.ITEM_MODEL_3D, Identifier.of(HorizonCommon.NAMESPACE, "compact-disk.png"));
    }

    @Override
    public int getDefaultStackLimit() {
        return 1;
    }

    @Override
    public String getName() {
        return "Compat Disc";
    }

    @Override
    public int getSize() {
        return ComputerConstants.BASIC_COMPACT_DISK_SIZE;
    }
}
