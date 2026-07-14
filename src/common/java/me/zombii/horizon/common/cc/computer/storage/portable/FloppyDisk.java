package me.zombii.horizon.common.cc.computer.storage.portable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class FloppyDisk extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "floppy-disk");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, FloppyDisk::new);
    }

    public FloppyDisk() {}

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return ComputerConstants.BASIC_FLOPPY_SIZE;
    }

}
