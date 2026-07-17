package me.zombii.horizon.common.cc.computer.storage.portable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.ComputerConstants;

import javax.naming.SizeLimitExceededException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PortableStorage extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "portable-storage");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, PortableStorage::new);
    }

    private int size;

    public PortableStorage() {
    }

    public PortableStorage(int size) {
        this.size = size;
    }

    @Override
    public void load(DataInputStream inputStream) throws IOException {
        this.size = inputStream.readInt();
        super.load(inputStream);
    }

    @Override
    public void save(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.size);
        super.save(outputStream);
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return size;
    }

    public static void write(PortableStorage device, int offset, byte[] bytes) throws SizeLimitExceededException {
        if (device.size < offset) throw new SizeLimitExceededException("Can't perform writes outside the device size range");
        if (device.size < (bytes.length + offset)) throw new SizeLimitExceededException("Can't perform writes larger than the storage");

        if (!device.isInitialized()) device.init();

        byte[] data = device.getData();
        System.arraycopy(bytes, 0, data, offset, bytes.length);
    }

}
