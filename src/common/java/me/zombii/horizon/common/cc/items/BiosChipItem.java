package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonCommon;

public class BiosChipItem extends AbstractCosmicItem {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "bios_chip");

    public BiosChipItem() {
        super(ID);
    }

    @Override
    public int getDefaultStackLimit() {
        return 1;
    }

}
