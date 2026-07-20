package me.zombii.horizon.common.cc.display;

import com.badlogic.gdx.utils.ByteArray;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;

public interface ICCScreen {

    int getWidth();
    int getHeight();
    ScreenType getScreenType();
    void swap();

    ICCScreen setUUID(UUID uuid);

    UUID getUUID();

    ICCPalette getPalette();

    void write(ByteArray dos);
    void read(ByteBuffer dis);

    byte getPixel(int x, int y);
    void setPixel(int x, int y, byte idx);
    void fill(byte idx);

    void update(ICCScreen screen);

    byte[] getFrameBuffer();

    byte[] getBackBuffer();

    void setOnSwap(Consumer<ICCScreen> onSwap);
}
