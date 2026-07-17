package me.zombii.horizon.common.cc.items;

import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.util.Identifier;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.cc.computer.ComputerConstants;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.computer.storage.portable.NonPortableStorage;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

public class HardDiskDriveItem extends AbstractDataStorageItem implements IPeripheralItem {

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
    public int getSize() {
        return ComputerConstants.BASIC_HDD_SIZE;
    }

    public static NonPortableStorage getStorage(ItemStack stack) {
        NonPortableStorage chip = (NonPortableStorage) AbstractDataStorageDevice.COMPONENTS_BY_ID.get(stack.stackMetadata.getInt("storage-id", -1));
        if (chip == null) {
            chip = new NonPortableStorage(
                    stack.getItem().getIntProperty("storage-size", -1)
            );
            chip.init();
            stack.setMetadataInt("storage-id", chip.getSlot());
        }

        return chip;
    }

    @Override
    public void register(SmartEventBusHandle handle, ItemStack stack) {
        IPeripheralItem.super.register(handle, stack);

        handle.registerEventHandler((fromAddress, toAddress, eventName, eventDataStr) -> {
            NonPortableStorage storage = getStorage(stack);

            if (fromAddress.equals(toAddress)) return;
            if (fromAddress.equals(handle.getAddress())) return;

            JsonValue data = JsonValue.readJSON(eventDataStr);

            ObjectSet<String> sessions = new ObjectOpenHashSet<>();

            switch (eventName) {
                case "cc:session" -> {
                    if (!data.isObject()) {
                        handle.postEvent(fromAddress, eventName, "{\"status\":\"rejected\"}");
                        return;
                    }
                    JsonObject dataObject = data.asObject();

                    String status = dataObject.getString("status", "request");

                    if (status.equals("request")) {
                        if (sessions.add(fromAddress)) {
                            handle.postEvent(fromAddress, eventName, "{\"status\":\"success\"}");
                        } else {
                            handle.postEvent(fromAddress, eventName, "{\"status\":\"duplicate\"}");
                        }
                    }
                    if (status.equals("terminate")) {
                        sessions.remove(fromAddress);
                        handle.postEvent(fromAddress, eventName, "{\"status\":\"terminated\"}");
                    }
                }
                case "cc:call" -> {
                    if (!sessions.contains(handle.getAddress())) return;
                    JsonObject dataObject = data.asObject();

                    String status = dataObject.getString("status", "request");
                    if (!status.equals("request")) return;

                    JsonValue fnName = dataObject.get("fn_name");
                    JsonValue fnArgs = dataObject.get("fn_args");

                    if (!fnName.isString()) {
                        handle.postEvent(fromAddress, "cc:call", "{\"status\":\"error\",\"message\":\"'fn_name' must be a string!\"}");
                        return;
                    }
                    if (!fnArgs.isArray()) {
                        handle.postEvent(fromAddress, "cc:call", "{\"status\":\"error\",\"message\":\"'fn_name' must be an array!\"}");
                        return;
                    }

                    JsonArray args = fnArgs.asArray();

                    switch (fnName.asString()) {
                        case "readInt" -> {
                            if (args.size() != 1 || !args.get(0).isNumber()) {
                                handle.postEvent(fromAddress, "cc:call", "{\"status\":\"error\",\"message\":\"'readInt' expected 1 number argument.\"}");
                                return;
                            }
                            int position = args.get(0).asInt();
                            handle.postEvent(fromAddress, "cc:call", "{\"status\":\"response\",\"output\":["+storage.readInt(position)+"]}");
                            return;
                        }
                    }
                }
            }

        });
    }

    @Override
    public String getType() {
        return "cc:storage-device";
    }

}
