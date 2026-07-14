package me.zombii.horizon.common.cc.packets;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.Identifier;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.zombii.horizon.common.IHorizonClientBound;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class PacketOpenScreen extends GamePacket {

    private ScreenOpenInfo screenOpenInfo;

    public PacketOpenScreen() {}

    public PacketOpenScreen(ScreenOpenInfo screenOpenInfo) {
        this.screenOpenInfo = screenOpenInfo;
    }

    @Override
    public void receive(ByteBuf in) {
        Player player = ServerSingletons.getAccountByUniqueId(readString(in)).getPlayer();
        Identifier screenId = Identifier.of(readString(in));
        BlockPosition blockPosition = readBlockPosition(in, player.getZone());
        CRBinDeserializer deserializer = new CRBinDeserializer();
        deserializer.prepareForRead(in.nioBuffer());
        ItemStack stack = deserializer.readObj("heldStack", ItemStack.class);

        screenOpenInfo = new ScreenOpenInfo(player, screenId, blockPosition, stack);
    }

    @Override
    public void write() {
        writeString(screenOpenInfo.player().getAccount().getUniqueId());
        writeString(screenOpenInfo.screenId().toString());
        writeBlockPosition(screenOpenInfo.position());
        CRBinSerializer serial = new CRBinSerializer();
        serial.writeObj("heldStack", screenOpenInfo.stack());
    }

    @Override
    public void handle(NetworkIdentity networkIdentity, ChannelHandlerContext channelHandlerContext) {
        if (networkIdentity.isClient()) {
            IHorizonClientBound.INSTANCE.get().openScreen(screenOpenInfo);
        }
    }

}
