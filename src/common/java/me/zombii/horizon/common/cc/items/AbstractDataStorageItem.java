package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.util.GameTag;
import finalforeach.cosmicreach.util.GameTagList;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.impl.data.point.single.IntegerDataPoint;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonTags;

public abstract class AbstractDataStorageItem extends AbstractCosmicItem {

    public AbstractDataStorageItem(Identifier id, boolean portable) {
        super(id);
        list.add(portable ? HorizonTags.TAG_PORTABLE_STORAGE : HorizonTags.TAG_NON_PORTABLE_STORAGE);
        manifest.put("storage-size", new IntegerDataPoint(getSize()));
    }

    @Override
    public GameTagList getTags() {
        return list;
    }

    abstract public int getSize();
}
