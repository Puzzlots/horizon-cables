package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.impl.data.point.single.IntegerDataPoint;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class FloppyDiskItem extends AbstractDataStorageItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "floppy-disk");

    public FloppyDiskItem() {
        super(ID, false);
        addTexture(ItemModelType.ITEM_MODEL_3D, Identifier.of(HorizonCommon.NAMESPACE, "floppy-disk.png"));
    }

    @Override
    public int getDefaultStackLimit() {
        return 1;
    }

    @Override
    public String getName() {
        return "Floppy Disk";
    }

    @Override
    public int getSize() {
        return ComputerConstants.BASIC_FLOPPY_SIZE;
    }
}
