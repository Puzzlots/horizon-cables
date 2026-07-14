package me.zombii.horizon.common.cc.computer.storage.portable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

import javax.naming.OperationNotSupportedException;

public class MiniCDRomDisk extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "mini-cd-rom");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, MiniCDRomDisk::new);
    }

    private boolean canWriteTo = false;

    public MiniCDRomDisk() {}

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return ComputerConstants.BASIC_MINI_CD_SIZE;
    }

    public boolean canWriteTo() {
        return canWriteTo;
    }

    public static void burn(MiniCDRomDisk romDisk, byte[] bytes) throws OperationNotSupportedException {
        if (!romDisk.canWriteTo) throw new OperationNotSupportedException("Can't burn cd-rom that already was burnt!");
        romDisk.canWriteTo = false;

        if (!romDisk.isInitialized()) romDisk.init();

        byte[] data = romDisk.getData();
        System.arraycopy(bytes, 0, data, 0, bytes.length);
    }

}
