package me.zombii.horizon.common.cc.computer.storage.portable;

import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;

import javax.naming.SizeLimitExceededException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BasicStorage extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "basic-storage");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, BasicStorage::new);
    }

    private int size;
    private boolean readOnly;

    public BasicStorage() {}

    public BasicStorage(int size) {
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

    public static void write(BasicStorage device, int offset, byte[] bytes) throws SizeLimitExceededException {
        if (device.size < offset) throw new SizeLimitExceededException("Can't perform writes outside the device size range");
        if (device.size < (bytes.length + offset)) throw new SizeLimitExceededException("Can't perform writes larger than the storage");

        if (!device.isInitialized()) device.init();

        byte[] data = device.getData();
        System.arraycopy(bytes, 0, data, offset, bytes.length);
    }

}
