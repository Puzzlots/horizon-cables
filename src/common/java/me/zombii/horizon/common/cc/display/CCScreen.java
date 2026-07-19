package me.zombii.horizon.common.cc.display;

import com.badlogic.gdx.utils.ByteArray;
import finalforeach.cosmicreach.io.ByteArrayUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CCScreen implements ICCScreen {

    private final ICCPalette palette;
    private int width;
    private int height;
    private final AtomicReference<byte[]> frameBuffer;
    private final AtomicReference<byte[]> backBuffer;

    private Consumer<ICCScreen> onSwap;
    public static final Consumer<ICCScreen> EMPTY = (c) -> {};

    private UUID uuid;

    public CCScreen(
            int width, int height, ICCPalette palette
    ) {
        this(width, height, palette, EMPTY);
    }

    public CCScreen(
            int width, int height, ICCPalette palette,
            Consumer<ICCScreen> onSwap
    ) {
        this.width = width;
        this.height = height;
        this.frameBuffer = new AtomicReference<>(new byte[width * height]);
        this.backBuffer = new AtomicReference<>(new byte[width * height]);
        this.onSwap = onSwap;
        this.palette = palette;
        setUUID(UUID.randomUUID());
    }

    @Override
    public ICCScreen setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public byte[] getFrameBuffer() {
        return frameBuffer.get();
    }

    public byte[] getBackBuffer() {
        return backBuffer.get();
    }

    public void setOnSwap(Consumer<ICCScreen> onSwap) {
        this.onSwap = onSwap;
    }

    @Override
    public void swap() {
        this.backBuffer.set(this.frameBuffer.getAndSet(this.backBuffer.get()));
        onSwap.accept(this);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.MONOCHROME;
    }

    @Override
    public ICCPalette getPalette() {
        return palette;
    }

    public void write(ByteArray dos) {
        getPalette().write(dos);

        byte[] bytes = frameBuffer.get();
        for (byte aByte : bytes) {
            ByteArrayUtils.writeByte(dos, aByte);
        }
    }

    public void read(ByteBuffer dis) {
        getPalette().read(dis);

        byte[] buf = backBuffer.get();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = ByteArrayUtils.readByte(dis);
        }
        swap();
    }

    @Override
    public byte getPixel(int x, int y) {
        return getFrameBuffer()[x + (width * y)];
    }

    @Override
    public void setPixel(int x, int y, byte idx) {
        getFrameBuffer()[x + (width * y)] = idx;
    }

    @Override
    public void fill(byte idx) {
        Arrays.fill(backBuffer.get(), idx);
    }

    private static final Object2ObjectMap<UUID, ICCScreen> SCREEN_CACHE = new Object2ObjectOpenHashMap<>();

    public static ICCScreen getOrMake(UUID uuid, ICCPalette palette, int width, int height) {
        ICCScreen c = SCREEN_CACHE.get(uuid);
        if (c == null) {
            c = new CCScreen(width, height, palette);
            c.setUUID(uuid);
            SCREEN_CACHE.put(uuid, c);
        }
        return c;
    }

}
