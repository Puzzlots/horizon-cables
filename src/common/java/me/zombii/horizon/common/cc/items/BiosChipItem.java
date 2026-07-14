package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonCommon;

public class BiosChipItem extends AbstractCosmicItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "bios_chip");

    public BiosChipItem() {
        super(ID);
        addTexture(ItemModelType.ITEM_MODEL_3D, Identifier.of(HorizonCommon.NAMESPACE, "bios_chip.png"));
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
