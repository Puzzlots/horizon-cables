package me.zombii.horizon.common.cc.display;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ByteArray;
import finalforeach.cosmicreach.io.ByteArrayUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class CCPalette implements ICCPalette {

    private UUID uuid;
    private int size;
    private final short[] palette;

    public CCPalette(int size) {
        this.size = size;
        this.palette = new short[size];
        setUUID(UUID.randomUUID());
    }

    @Override
    public ICCPalette setColor(int idx, short color) {
        palette[idx] = color;
        return this;
    }

    @Override
    public ICCPalette setColors(int srcStart, int srcLength, short[] srcPalette, int destOffset) {
        System.arraycopy(srcPalette, srcStart, palette, destOffset, srcLength);
        return this;
    }

    @Override
    public short getColor(int idx) {
        return palette[idx];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void write(ByteArray dos) {
        ByteArrayUtils.writeShort(dos, size);
        for (short value : palette) {
            ByteArrayUtils.writeShort(dos, value);
        }
    }

    @Override
    public void read(ByteBuffer dis) {
        size = ByteArrayUtils.readShort(dis);
        for (int i = 0; i < palette.length; i++) {
            palette[i] = ByteArrayUtils.readShort(dis);
        }
    }

    @Override
    public ICCPalette setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void update(ICCPalette palette) {
        if (!getUUID().equals(palette.getUUID())) return;

        if (Arrays.equals(((CCPalette)palette).palette, this.palette)) return;
        System.arraycopy(((CCPalette)palette).palette, 0, this.palette, 0, size);
    }

    public static final ICCPalette DEFAULT_MONOCHROME = new CCPalette(2)
            .setColor(1, (short) 0xFFFF);

    private static final Object2ObjectMap<UUID, ICCPalette> PALETTE_CACHE = new Object2ObjectOpenHashMap<>();

    public static ICCPalette getOrMake(UUID uuid, int size) {
        ICCPalette c = PALETTE_CACHE.get(uuid);
        if (c == null) {
            c = new CCPalette(size);
            c.setUUID(uuid);
            PALETTE_CACHE.put(uuid, c);
        }
        return c;
    }

}
