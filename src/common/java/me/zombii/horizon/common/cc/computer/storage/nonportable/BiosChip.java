package me.zombii.horizon.common.cc.computer.storage.nonportable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;

import javax.naming.SizeLimitExceededException;
import java.nio.charset.StandardCharsets;

public class BiosChip extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "bios-chip");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, BiosChip::new);
    }

    public BiosChip() {}

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return 1024 * 25;
    }

    public static void flashChip(BiosChip chip, String luaCode) throws SizeLimitExceededException {
        byte[] bytes = luaCode.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > chip.getMaxDiskSize()) {
            throw new SizeLimitExceededException("Tried to write lua code that was " + (bytes.length - chip.getMaxDiskSize() - 4) + " over the max bytes of " + (chip.getMaxDiskSize() - 4));
        }

        if (!chip.isInitialized()) chip.init();

        byte[] data = chip.getData();
        System.arraycopy(bytes, 0, data, 0, bytes.length);
    }

}
