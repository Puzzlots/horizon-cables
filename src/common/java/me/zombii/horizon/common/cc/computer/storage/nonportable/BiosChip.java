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

    public static String getInitCode(BiosChip chip) {
        int entryCount = chip.readInt(0);
        int offs = 4;

        int initAddr = 0;
        int initSize = 0;

        for (int i = 0; i < entryCount; i++) {
            int nameLen = chip.readInt(offs);
            String name = new String(chip.getBytes(offs + 4, nameLen));
            offs += nameLen + 4;
            int size = chip.readInt(offs);
            int addr = chip.readInt(offs + 4);
            offs += 8;
            if (name.equals("init.lua")) {
                initSize = size;
                initAddr = addr;
            }
        }
        byte[] initCodeBytes = chip.getBytes(offs + initAddr, initSize);
        return new String(initCodeBytes, StandardCharsets.UTF_8);
    }

    public static void flashChip(BiosChip chip, String... files) throws SizeLimitExceededException {
        try {
            ByteArrayOutputStream header = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(header);
            dataOutputStream.writeInt(files.length / 2);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);
            int totalBytesWritten = 0;
            for (int i = 0; i < files.length; i += 2) {
                String file = files[i];
                String name = files[i + 1];

                RawAssetLoader.RawFileHandle handle = IndependentAssetLoader.loadAsset(Identifier.of(file));
                dataOutputStream.writeInt(name.length());
                dataOutputStream.write(name.getBytes(StandardCharsets.UTF_8));
                dataOutputStream.writeInt(handle.getBytes().length);
                dataOutputStream.writeInt(totalBytesWritten);
                totalBytesWritten += handle.getBytes().length;

                outputStream.write(handle.getBytes());
            }
            dataOutputStream.close();
            outputStream.close();

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            baos2.write(header.toByteArray());
            baos2.write(baos.toByteArray());

            byte[] bytes = baos2.toByteArray();

            if (bytes.length >= chip.getMaxDiskSize()) {
                throw new SizeLimitExceededException("Tried to write data that was " + (bytes.length - chip.getMaxDiskSize()) + " over the max bytes of " + chip.getMaxDiskSize());
            }

            chip.init();

            System.arraycopy(bytes, 0, chip.getData(), 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
