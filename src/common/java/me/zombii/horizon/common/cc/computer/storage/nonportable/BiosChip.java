package me.zombii.horizon.common.cc.computer.storage.nonportable;

import dev.puzzleshq.puzzleloader.cosmic.game.util.IndependentAssetLoader;
import dev.puzzleshq.puzzleloader.loader.util.RawAssetLoader;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;

import javax.naming.SizeLimitExceededException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BiosChip extends AbstractDataStorageDevice {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "bios-chip");

    public static void register() {
        HorizonRegistries.PC_COMPONENT_REGISTRY.store(ID, BiosChip::new);
    }

    private int maxSize;

    public BiosChip() {}

    public BiosChip(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void save(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(maxSize);
        super.save(outputStream);
    }

    @Override
    public void load(DataInputStream inputStream) throws IOException {
        maxSize = inputStream.readInt();
        super.load(inputStream);
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public int getMaxDiskSize() {
        return maxSize;
    }

    public static void flashChip(BiosChip chip, String... files) throws SizeLimitExceededException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);
        try {
            int totalBytesWritten = 0;
            for (String file : files) {
                RawAssetLoader.RawFileHandle handle = IndependentAssetLoader.loadAsset(Identifier.of(file));
                totalBytesWritten += handle.getBytes().length + 4;
                if (totalBytesWritten >= chip.getMaxDiskSize()) {
                    throw new SizeLimitExceededException("Tried to write lua code that was " + (totalBytesWritten - chip.getMaxDiskSize()) + " over the max bytes of " + chip.getMaxDiskSize() + ": " + file);
                }

                outputStream.writeInt(handle.getBytes().length);
                outputStream.write(handle.getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        chip.init();

        byte[] bytes = baos.toByteArray();
        System.arraycopy(bytes, 0, chip.getData(), 0, bytes.length);
    }

    public static void flashChip(BiosChip chip, String luaCode) throws SizeLimitExceededException {
        byte[] bytes = luaCode.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > chip.getMaxDiskSize()) {
            throw new SizeLimitExceededException("Tried to write lua code that was " + (bytes.length - chip.getMaxDiskSize() - 4) + " over the max bytes of " + (chip.getMaxDiskSize() - 4));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(baos);
        try {
            stream.writeInt(bytes.length);
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        chip.init();

        byte[] newData = baos.toByteArray();

        byte[] data = chip.getData();
        System.arraycopy(newData, 0, data, 0, newData.length);
    }

}
