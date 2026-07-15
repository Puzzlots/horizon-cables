package me.zombii.horizon.common.cc.packets;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.singletons.GameSingletons;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.zombii.horizon.common.cc.blocks.BiosFlasherBlockEntity;

public class PacketFlashBIOS extends GamePacket {

    public PacketFlashBIOS() {
    }

    private final BlockPosition position = new BlockPosition();

    public PacketFlashBIOS(BiosFlasherBlockEntity be) {
        position.setGlobal(be.getZone(), be.getGlobalX(), be.getGlobalY(), be.getGlobalZ());
    }

    @Override
    public void receive(ByteBuf in) {
        position.setGlobal(
                GameSingletons.world.getZoneIfExists(readString(in)),
                readInt(in),
                readInt(in),
                readInt(in)
        );
    }

    @Override
    public void write() {
        writeString(position.getZone().zoneId);
        writeInt(position.getGlobalX());
        writeInt(position.getGlobalY());
        writeInt(position.getGlobalZ());
    }

    @Override
    public void handle(NetworkIdentity networkIdentity, ChannelHandlerContext channelHandlerContext) {
        if (networkIdentity.isServer() && position.getBlockEntity() instanceof BiosFlasherBlockEntity entity) {
            entity.flashChip(networkIdentity.getPlayer());
        }
    }

}
