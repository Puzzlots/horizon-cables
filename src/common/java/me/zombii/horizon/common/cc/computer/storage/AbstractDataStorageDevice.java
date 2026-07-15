package me.zombii.horizon.common.cc.computer.storage;

import finalforeach.cosmicreach.util.Identifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.zombii.horizon.common.HorizonRegistries;
import me.zombii.horizon.common.cc.computer.peripherals.AbstractPeripheral;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class AbstractDataStorageDevice extends AbstractPeripheral {

    public static final ObjectList<AbstractDataStorageDevice> COMPONENTS = new ObjectArrayList<>();
    public static final Int2ObjectMap<AbstractDataStorageDevice> COMPONENTS_BY_ID = new Int2ObjectArrayMap<>();

    public static final IntList deletedSlots = new IntArrayList();
    public static int nextSlot = 0;

    private byte[] data;
    private boolean initialized;
    private int slot = -1;
    public abstract Identifier getID();

    public void save(DataOutputStream outputStream) throws IOException {
        if (!initialized) init();
        outputStream.write(data);
    }

    public void load(DataInputStream inputStream) throws IOException {
        if (!initialized) init();
        inputStream.read(data);
    }

    public void delete() {
        deletedSlots.add(slot);
        COMPONENTS.remove(this);
        COMPONENTS_BY_ID.remove(slot, this);
    }

    public static void saveComponents(File worldLocation) throws IOException {
        File partFolder = new File(worldLocation, "pc_components");
        File partIndex = new File(partFolder, "index.bin");
        if (!partFolder.exists()) partFolder.mkdir();
        if (!partIndex.exists()) partIndex.createNewFile();

        FileOutputStream indexStream = new FileOutputStream(partIndex);
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(indexStream);
        DataOutputStream dataOutputStream = new DataOutputStream(gzipOutputStream);
        dataOutputStream.writeInt(nextSlot);
        dataOutputStream.writeInt(deletedSlots.size());
        for (Integer deletedSlot : deletedSlots) {
            dataOutputStream.writeInt(deletedSlot);

            File removedComponentFile = new File(partFolder,  "data-" + deletedSlot + ".bin");
            if (removedComponentFile.exists()) {
                removedComponentFile.delete();
            }
        }

        int savableComponents = 0;
        for (AbstractDataStorageDevice component : COMPONENTS) {
            if (component.isInitialized()) savableComponents++;
        }

        dataOutputStream.writeInt(savableComponents);

        for (AbstractDataStorageDevice component : COMPONENTS) {
            if (component.isInitialized()) {
                File componentFile = new File(partFolder,  "data-" + component.slot + ".bin");
                if (!componentFile.exists()) componentFile.createNewFile();

                FileOutputStream stream = new FileOutputStream(componentFile);
                GZIPOutputStream gzipStream = new GZIPOutputStream(stream);
                DataOutputStream dataStream = new DataOutputStream(gzipStream);
                component.save(dataStream);
                dataStream.close();
                dataOutputStream.writeUTF(component.getID().toString());
                dataOutputStream.writeInt(component.slot);
            }
        }
        dataOutputStream.close();
    }

    public static void loadComponents(File worldLocation) throws IOException {
        COMPONENTS.clear();

        File partFolder = new File(worldLocation, "pc_components");
        if (!partFolder.exists()) return;

        File partIndex = new File(partFolder, "index.bin");

        FileInputStream indexStream = new FileInputStream(partIndex);
        GZIPInputStream gzipInputStream = new GZIPInputStream(indexStream);
        DataInputStream dataInputStream = new DataInputStream(gzipInputStream);

        nextSlot = dataInputStream.readInt();
        deletedSlots.clear();
        int deletedSlotsSize = dataInputStream.readInt();
        for (int i = 0; i < deletedSlotsSize; i++) {
            deletedSlots.add(dataInputStream.readInt());
        }

        int savedComponentsCount = dataInputStream.readInt();
        for (int i = 0; i < savedComponentsCount; i++) {
            Identifier componentID = Identifier.of(dataInputStream.readUTF());
            int slot = dataInputStream.readInt();

            AbstractDataStorageDevice component = HorizonRegistries.PC_COMPONENT_REGISTRY.get(componentID).get();
            component.slot = slot;

            File componentFile = new File(partFolder,  "data-" + slot + ".bin");
            if (!componentFile.exists()) {
                System.out.println("Missing component file for ID: " + slot);
                continue;
            }

            FileInputStream stream = new FileInputStream(componentFile);
            GZIPInputStream gzipStream = new GZIPInputStream(stream);
            DataInputStream dataStream = new DataInputStream(gzipStream);

            component.load(dataStream);
            dataStream.close();
            COMPONENTS.add(component);
        }
        gzipInputStream.close();
    }

    public void init() {
        if (initialized) return;
        initialized = true;

        data = new byte[getMaxDiskSize()];
        if (slot == -1) {
            if (deletedSlots.isEmpty()) {
                slot = nextSlot++;
            } else {
                slot = deletedSlots.removeLast();
            }
        }

        COMPONENTS.add(this);
        COMPONENTS_BY_ID.put(slot, this);
    }

    public abstract int getMaxDiskSize();

    public byte[] getData() {
        return data;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getSlot() {
        return slot;
    }
}
