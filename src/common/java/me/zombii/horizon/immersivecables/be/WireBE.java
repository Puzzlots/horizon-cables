package me.zombii.horizon.immersivecables.be;

import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.AbstractEnergyBE;
import me.zombii.horizon.immersivecables.IEnergyBE;

public class WireBE extends AbstractEnergyBE {

    public static final String ID = "horizon:wire-block-entity";
    public static final String UNIVERSAL_CHANNEL = "universal";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, (blockState, zone, x, y, z) -> {
            Block block = blockState.getBlock();
            String channel = getBlockEntityParamString(block, "channel");
            return new WireBE(blockState, zone, x, y, z, channel);
        });
    }

    private String channel;
    private final Direction[] ports = Direction.ALL_DIRECTIONS;

    public WireBE() {}

    public WireBE(BlockState state, Zone zone, int gX, int gY, int gZ, String channel) {
        super(state, zone, gX, gY, gZ);
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public Direction[] getPorts() {
        return ports;
    }

    @Override
    public boolean canConnect(BlockState state, BlockState target, BlockEntity beTarget, Direction direction) {
        if (beTarget instanceof WireBE be) {
            return this.getChannel() != null && (this.getChannel().equals(be.getChannel()) || (be.isUniversal() || isUniversal()));
        }

        IGameTagList list = target.getTags();
        return (list != null && list.contains(HorizonTags.TAG_CABLE_CONNECTABLE)) || beTarget instanceof IEnergyBE;
    }

    private boolean isUniversal() {
        return channel != null && channel.equals(UNIVERSAL_CHANNEL);
    }

    @Override
    public BlockPosition getBlockPosition() {
        return BlockPosition.ofGlobal(getZone(), getGlobalX(), getGlobalY(), getGlobalZ());
    }

    @Override
    public void doTurnOff(Direction direction) {
        super.doTurnOff(direction);
        for (Direction port : getPorts()) {
            if (canConnect(port)) {
                int gX = getGlobalX() + port.getXOffset();
                int gY = getGlobalY() + port.getYOffset();
                int gZ = getGlobalZ() + port.getZOffset();

                BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);
                if (!(entity instanceof IEnergyBE energyBE)) continue;
                energyBE.turnOff(port);
            }
        }
    }

    @Override
    public void doTurnOn(Direction direction) {
        super.doTurnOn(direction);
        for (Direction port : getPorts()) {
            if (canConnect(port)) {
                int gX = getGlobalX() + port.getXOffset();
                int gY = getGlobalY() + port.getYOffset();
                int gZ = getGlobalZ() + port.getZOffset();

                BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);
                if (!(entity instanceof IEnergyBE energyBE)) continue;
                energyBE.turnOn(port);
            }
        }
    }

    @Override
    public String getBlockEntityId() {
        return ID;
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        serial.writeString("channel", channel);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        channel = deserial.readString("color");
        if (channel == null)
            channel = deserial.readString("channel");
        if (channel == null) channel = UNIVERSAL_CHANNEL;
    }

}