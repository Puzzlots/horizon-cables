package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class HardDiskDriveItem extends AbstractDataStorageItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "hard-disk-drive");

    public HardDiskDriveItem() {
        super(ID, false);
        addTexture(ItemModelType.ITEM_MODEL_3D, Identifier.of(HorizonCommon.NAMESPACE, "hard-disk-drive.png"));
    }

    @Override
    public int getDefaultStackLimit() {
        return 1;
    }

    @Override
    public String getName() {
        return "Hard Disk Drive";
    }

    @Override
    public String getPeripheralID() {
        return "cc:hard-disk-drive";
    }

    @Override
    public int getSize() {
        return ComputerConstants.BASIC_HDD_SIZE;
    }

}
