package me.zombii.horizon.immersivecables.be;

import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.AbstractEnergyBE;
import me.zombii.horizon.immersivecables.IEnergyBE;
import me.zombii.horizon.immersivecables.ImEventManager;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClockBE extends AbstractEnergyBE {

    public static final String ID = "horizon:clock-block-entity";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, ClockBE::new);
    }

    private Direction[] outPorts;
    private boolean rotatePorts;
    private Direction facingDirection;
    private boolean invertOutputs;

    public ClockBE() {}

    public ClockBE(BlockState state, Zone zone, int gX, int gY, int gZ) {
        super(state, zone, gX, gY, gZ);
        Boolean invOut = getBlockEntityParam(getBlockState().getBlock(), "invertOutputs");
        invertOutputs = invOut != null && invOut;
        setTicking(true);
    }

    @Override
    public void onTick() {
        super.onTick();
        if ((getZone().getCurrentWorldTick() & 1) == 0) {
            isOn = ((getZone().getCurrentWorldTick() >> 1) & 1) == 1;
            sendPowerOut(isOn);
        }
    }

    @Override
    public void initBE() {
        super.initBE();
    }

    private final Direction[] inPorts = new Direction[]{};

    @Override
    public Direction[] getPorts() {
        initPorts();
        return inPorts;
    }

    @Override
    public boolean canConnect(BlockState state, BlockState target, BlockEntity beTarget, Direction direction) {
        initPorts();
        if (Arrays.stream(outPorts).noneMatch(i -> i == direction)) return false;

        IGameTagList list = target.getTags();
        return (list != null && list.contains(HorizonTags.TAG_CABLE_CONNECTABLE)) || beTarget instanceof IEnergyBE;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    private void sendPowerOut(boolean powered) {
        initPorts();

        boolean isOn = powered == !this.invertOutputs;
        for (Direction port : outPorts) {
            if (canConnect(port)) {
                int gX = getGlobalX() + port.getXOffset();
                int gY = getGlobalY() + port.getYOffset();
                int gZ = getGlobalZ() + port.getZOffset();

                BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);

                if (entity instanceof IEnergyBE ebe) {
                    ImEventManager.queueEvent(getZone(), gX, gY, gZ, isOn, port);
//                    if (isOn) ebe.turnOn(port);
//                    else ebe.turnOff(port);
                }
            }
        }
    }

    @Override
    public void doTurnOn(Direction direction) {
    }

    @Override
    public void doTurnOff(Direction direction) {
    }

    public void initPorts() {
        Direction currentFacing = getBlockState().getParamDirection("direction");
        rotatePorts = getBlockEntityParam(getBlockState().getBlock(), "rotatePorts");
        if (currentFacing != this.facingDirection) {
            this.facingDirection = currentFacing;
            outPorts = null;
        }
        if (outPorts == null) {
            Array<String> directions = getBlockEntityParam(getBlockState().getBlock(), "outPorts");
            this.outPorts = new Direction[directions.size];
            for (int i = 0; i < directions.size; i++) {
                String direction = directions.get(i);
                try {
                    this.outPorts[i] = Direction.fromStr(direction);
                } catch (Exception e) {
                    this.outPorts[i] = Direction.valueOf(direction.toUpperCase());
                }
            }
            rotatePorts(rotatePorts, facingDirection, outPorts);
        }
    }

    @Override
    public String getBlockEntityId() {
        return ID;
    }

    @Override
    public void write(CRBinSerializer serial) {
        serial.writeBoolean("invertOutputs", invertOutputs);
        super.write(serial);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        invertOutputs = deserial.readBoolean("invertOutputs", false);
        super.read(deserial);
    }

}