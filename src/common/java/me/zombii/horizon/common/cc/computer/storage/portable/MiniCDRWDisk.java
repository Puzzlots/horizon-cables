package me.zombii.horizon.common.cc.computer.storage.portable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

public class MiniCDRWDisk extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "mini-cd-rw");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, MiniCDRWDisk::new);
    }

    public MiniCDRWDisk() {}

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return ComputerConstants.BASIC_MINI_CD_SIZE;
    }

}
