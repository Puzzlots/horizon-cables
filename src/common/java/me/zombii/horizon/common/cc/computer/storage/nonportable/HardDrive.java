package me.zombii.horizon.common.cc.computer.storage.nonportable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class HardDrive extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "hard-drive");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, HardDrive::new);
    }

    public HardDrive() {}

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return ComputerConstants.BASIC_HDD_SIZE;
    }

}
