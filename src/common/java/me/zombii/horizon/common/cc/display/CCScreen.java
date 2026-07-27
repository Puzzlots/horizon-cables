package me.zombii.horizon.common.cc.display;

import com.badlogic.gdx.utils.ByteArray;
import finalforeach.cosmicreach.io.ByteArrayUtils;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletonPlayers;
import finalforeach.cosmicreach.singletons.GameSingletons;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.zombii.horizon.common.cc.packets.PacketScreenState;

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

    @Override
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
        byte[] back = this.backBuffer.get();
        byte[] front = this.frameBuffer.get();
        System.arraycopy(back, 0, front, 0, front.length);
        this.backBuffer.set(front);
        this.frameBuffer.set(back);
        onSwap.accept(this);
        if (!GameSingletons.isClient()) {
            for (NetworkIdentity allNetId : ServerSingletons.getAllNetIds()) {
                System.out.println("Send " + allNetId.getPlayer().getUsername());
                allNetId.send(new PacketScreenState(this));
            }
        }
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
        getBackBuffer()[x + (width * y)] = idx;
    }

    @Override
    public void fill(byte idx) {
        Arrays.fill(backBuffer.get(), idx);
    }

    @Override
    public void update(ICCScreen screen) {
        if (!screen.getUUID().equals(this.uuid)) return;
        if (!screen.getPalette().getUUID().equals(getPalette().getUUID())) return;

        palette.update(screen.getPalette());
        System.arraycopy(getBackBuffer(), 0, screen.getBackBuffer(), 0, getBackBuffer().length);
        swap();
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
