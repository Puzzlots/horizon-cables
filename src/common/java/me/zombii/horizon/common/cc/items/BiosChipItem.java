package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.GameTagList;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.impl.data.point.DataPointManifest;
import io.github.puzzle.cosmic.impl.data.point.single.IntegerDataPoint;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class BiosChipItem extends AbstractCosmicItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "bios-chip");

    public BiosChipItem() {
        super(ID);
        addTexture(ItemModelType.ITEM_MODEL_3D, Identifier.of(HorizonCommon.NAMESPACE, "bios_chip.png"));

        manifest.put("bios-chip-size", new IntegerDataPoint(ComputerConstants.BASIC_BIOS_SIZE));
        list.add(HorizonTags.TAG_BIOS_CHIP);
    }

    @Override
    public DataPointManifest getPointManifest() {
        return manifest;
    }

    @Override
    public int getDefaultStackLimit() {
        return 1;
    }

    @Override
    public String getName() {
        return "Bios Chip";
    }

}
