package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.impl.data.point.DataPointManifest;
import io.github.puzzle.cosmic.impl.data.point.single.IntegerDataPoint;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.cc.computer.ComputerConstants;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

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
