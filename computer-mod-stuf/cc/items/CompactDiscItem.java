package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class CompactDiscItem extends AbstractDataStorageItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "compact-disc");

    public CompactDiscItem() {
        super(ID, true);
        addTexture(ItemModelType.ITEM_MODEL_3D, Identifier.of(HorizonCommon.NAMESPACE, "compact-disc.png"));
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
    public String getPeripheralID() {
        return "cc:compact-disc";
    }

    @Override
    public int getSize() {
        return ComputerConstants.BASIC_COMPACT_DISK_SIZE;
    }
}
