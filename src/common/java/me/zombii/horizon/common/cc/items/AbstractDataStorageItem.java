package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.util.GameTagList;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.impl.data.point.single.IntegerDataPoint;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.storage.portable.BasicStorage;
import me.zombii.horizon.common.cc.lua.LuaStorageApi;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import party.iroiro.luajava.Lua;

public abstract class AbstractDataStorageItem extends AbstractCosmicItem implements IPeripheralItem {

    public AbstractDataStorageItem(Identifier id, boolean requires_burner) {
        super(id);
        list.add(HorizonTags.TAG_STORAGE_DEVICE);
        if (requires_burner) {
            list.add(HorizonTags.TAG_REQUIRES_BURNER_TO_WRITE);
        }
        manifest.put("storage-size", new IntegerDataPoint(getSize()));
    }

    @Override
    public GameTagList getTags() {
        return list;
    }

    abstract public int getSize();

    public static BasicStorage getStorage(ItemStack stack) {
        BasicStorage chip = (BasicStorage) AbstractDataStorageDevice.COMPONENTS_BY_ID.get(stack.stackMetadata.getInt("storage-id", -1));
        if (chip == null) {
            chip = new BasicStorage(
                    stack.getItem().getIntProperty("storage-size", -1)
            );
            chip.init();
            stack.setMetadataInt("storage-id", chip.getSlot());
        }

        return chip;
    }

    @Override
    public boolean register(Lua L, SmartEventBusHandle handle, ItemStack stack) {
        IPeripheralItem.super.register(L, handle, stack);

        LuaStorageApi.push(L, getStorage(stack), getTags().contains(HorizonTags.TAG_REQUIRES_BURNER_TO_WRITE));
        return true;
    }

    @Override
    public String getPeripheralType() {
        return "cc:storage-device";
    }
}
