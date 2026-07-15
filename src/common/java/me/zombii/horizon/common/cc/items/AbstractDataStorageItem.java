package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.GameTagList;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.impl.data.point.single.IntegerDataPoint;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonTags;

public abstract class AbstractDataStorageItem extends AbstractCosmicItem {

    public AbstractDataStorageItem(Identifier id) {
        super(id);
        list.add(HorizonTags.TAG_DATA_STORAGE_DEVICE);
        manifest.put("storage-size", new IntegerDataPoint(getSize()));
    }

    @Override
    public GameTagList getTags() {
        return list;
    }

    abstract public int getSize();
}
