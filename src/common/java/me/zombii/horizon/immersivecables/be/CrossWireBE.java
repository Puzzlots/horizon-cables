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
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.AbstractEnergyBE;
import me.zombii.horizon.immersivecables.IEnergyBE;
import me.zombii.horizon.immersivecables.ImEventManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CrossWireBE extends AbstractEnergyBE {

    public static final String ID = "horizon:cross-wire-block-entity";
    public static final String UNIVERSAL_CHANNEL = "universal";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, (blockState, zone, x, y, z) -> {
            Block block = blockState.getBlock();
            String channel = getBlockEntityParamString(block, "channel");
            return new CrossWireBE(blockState, zone, x, y, z, channel);
        });
    }

    private String channel;
    private final Direction[] ports = Direction.ALL_DIRECTIONS;

    public CrossWireBE() {}

    public CrossWireBE(BlockState state, Zone zone, int gX, int gY, int gZ, String channel) {
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
        if (beTarget instanceof CrossWireBE be) {
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
    public void turnOn(Direction direction) {
        doTurnOff(direction);
    }

    @Override
    public void turnOff(Direction direction) {
        doTurnOn(direction);
    }

    public void findAndPower(boolean doTurnOn, Direction origin) {
        Queue<IEnergyBE> positions = new ConcurrentLinkedDeque<>();
        ObjectSet<IEnergyBE> entities = new ObjectOpenHashSet<>();
        Queue<IEnergyBE> toModify = new ConcurrentLinkedDeque<>();
        Queue<Direction> dirList = new ConcurrentLinkedDeque<>();
        Queue<Direction> originList = new ConcurrentLinkedDeque<>();

        positions.add(this);
        originList.add(origin);
        while (!positions.isEmpty()) {
            IEnergyBE entity = positions.poll();
            Direction org = originList.poll();
            if (!entities.add(entity)) continue;

//            System.err.println(doTurnOn + " " + entity.getGlobalX() + " " + entity.getGlobalY() + " " + entity.getGlobalZ() + " " + entity);
            if (entity instanceof WireBE wireBE) {
                wireBE.isOn = doTurnOn;
                for (Direction port : wireBE.getPorts()) {
                    if (wireBE.canConnect(port)) {
                        int gX = wireBE.getGlobalX() + port.getXOffset();
                        int gY = wireBE.getGlobalY() + port.getYOffset();
                        int gZ = wireBE.getGlobalZ() + port.getZOffset();

                        BlockEntity entityCon = wireBE.getZone().getBlockEntity(gX, gY, gZ);
                        if (!(entityCon instanceof IEnergyBE energyBE)) continue;
//                        System.err.println(doTurnOn + " " +wireBE + " " + energyBE);
                        if (entityCon instanceof WireBE || entityCon instanceof CrossWireBE) {
                            positions.add(energyBE);
                            originList.add(port);
                        } else {
                            toModify.add(energyBE);
                            dirList.add(port);
                        }
                    }
                }
                continue;
            }
            if (entity instanceof CrossWireBE crossWireBE) {
                int nextX = crossWireBE.getGlobalX() + org.getXOffset();
                int nextY = crossWireBE.getGlobalY() + org.getYOffset();
                int nextZ = crossWireBE.getGlobalZ() + org.getZOffset();

                BlockEntity be = crossWireBE.getZone().getBlockEntity(nextX, nextY, nextZ);
                if (!(be instanceof IEnergyBE energyBE)) continue;
                if (be instanceof WireBE || be instanceof CrossWireBE) {
                    positions.add(energyBE);
                    originList.add(org);
                } else {
                    toModify.add(energyBE);
                    dirList.add(org);
                }
                continue;
            }
            throw new IllegalStateException("How did I get here?");
        }

        while (!toModify.isEmpty()) {
            IEnergyBE energyBE = toModify.poll();
            Direction direction = dirList.poll();
            ImEventManager.queueEvent(getZone(),
                    energyBE.getGlobalX(),
                    energyBE.getGlobalY(),
                    energyBE.getGlobalZ(),
                    doTurnOn, direction
            );
//            if (doTurnOn) {
//                energyBE.turnOn(direction);
//            } else {
//                energyBE.turnOff(direction);
//            }
        }
    }

    @Override
    public void doTurnOff(Direction direction) {
        super.doTurnOff(direction);
        findAndPower(false, direction);
//        for (Direction port : getPorts()) {
//            if (canConnect(port)) {
//                int gX = getGlobalX() + port.getXOffset();
//                int gY = getGlobalY() + port.getYOffset();
//                int gZ = getGlobalZ() + port.getZOffset();
//
//                BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);
//                if (!(entity instanceof IEnergyBE energyBE)) continue;
//                energyBE.turnOff(port);
//            }
//        }
    }

    @Override
    public void doTurnOn(Direction direction) {
        super.doTurnOn(direction);
        findAndPower(true, direction);
//        for (Direction port : getPorts()) {
//            if (canConnect(port)) {
//                int gX = getGlobalX() + port.getXOffset();
//                int gY = getGlobalY() + port.getYOffset();
//                int gZ = getGlobalZ() + port.getZOffset();
//
//                BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);
//                if (!(entity instanceof IEnergyBE energyBE)) continue;
//                energyBE.turnOn(port);
//            }
//        }
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