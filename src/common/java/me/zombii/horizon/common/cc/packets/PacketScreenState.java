package me.zombii.horizon.common.cc.packets;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.zombii.horizon.common.cc.display.CCPalette;
import me.zombii.horizon.common.cc.display.CCScreen;
import me.zombii.horizon.common.cc.display.ICCPalette;
import me.zombii.horizon.common.cc.display.ICCScreen;

import java.util.UUID;

public class PacketScreenState extends GamePacket {

    public PacketScreenState() {
    }

    private ICCScreen screen;
    private BlockPosition position;

    public PacketScreenState(ICCScreen screen, BlockPosition position) {
        this.screen = screen;
        this.position = position;
    }

    @Override
    public void receive(ByteBuf byteBuf) {
        long uuidAA = byteBuf.readLong();
        long uuidAB = byteBuf.readLong();
        long uuidBA = byteBuf.readLong();
        long uuidBB = byteBuf.readLong();

        UUID uuidA = new UUID(uuidAA, uuidAB);
        UUID uuidB = new UUID(uuidBA, uuidBB);

        int width = byteBuf.readInt();
        int height = byteBuf.readInt();
        int paletteSize = byteBuf.readInt();

        ICCPalette palette = CCPalette.getOrMake(uuidA, paletteSize);
        screen = CCScreen.getOrMake(uuidB, palette, width, height);

        position = readBlockPositionZoneless(byteBuf);
    }

    @Override
    public void write() {
        writeLong(screen.getUUID().getMostSignificantBits());
        writeLong(screen.getUUID().getLeastSignificantBits());
        writeLong(screen.getPalette().getUUID().getMostSignificantBits());
        writeLong(screen.getPalette().getUUID().getLeastSignificantBits());

        writeInt(screen.getWidth());
        writeInt(screen.getHeight());
        writeInt(screen.getPalette().getSize());
        screen.write(backingArray);

        writeBlockPosition(position);
    }

    @Override
    public void handle(NetworkIdentity networkIdentity, ChannelHandlerContext channelHandlerContext) {
        if (networkIdentity.isServer()) return;

        Player player = networkIdentity.getPlayer();
        position.setGlobal(player.getZone(), position.getGlobalX(), position.getGlobalY(), position.getGlobalZ());
    }

}
