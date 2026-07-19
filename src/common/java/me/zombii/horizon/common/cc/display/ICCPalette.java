package me.zombii.horizon.common.cc.display;

import com.badlogic.gdx.utils.ByteArray;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface ICCPalette {

    ICCPalette setColor(int idx, short color); // starts from 1, 0 is black

    ICCPalette setColors(int srcStart, int srcLength, short[] srcPalette, int destOffset);

    short getColor(int idx);

    int getSize();

    void write(ByteArray dos);
    void read(ByteBuffer dis);

    ICCPalette setUUID(UUID uuid);

    UUID getUUID();
}
