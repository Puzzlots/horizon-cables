package me.zombii.horizon.immersivecables.be;

import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.AbstractEnergyBE;
import me.zombii.horizon.immersivecables.IEnergyBE;
import me.zombii.horizon.immersivecables.PulseCondition;

import java.util.Arrays;
import java.util.Locale;

public class LaserPulserBE extends AbstractEnergyBE {

    public static final String ID = "horizon:laser-pulser-block-entity";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, LaserPulserBE::new);
    }

    private Direction[] inPorts;
    private Direction[] outPorts;
    private boolean rotatePorts;
    private Direction facingDirection;
    private PulseCondition condition;

    /*
    * front  POS_Z
    * back   NEG_Z
    * top    POS_Y
    * bottom NEG_Y
    * right  POS_X
    * left   NEG_X
    */

    private void rotatePorts(Direction[] directions) {
        if (!rotatePorts) return;
        if (facingDirection == Direction.POS_Z) return;
        switch (facingDirection) {
            case NEG_Z: {
                for (int i = 0; i < directions.length; i++) {
                    Direction dir = directions[i];
                    if (dir.isYAxis()) continue;
                    directions[i] = dir.getOpposite();
                }
                break;
            }
            case POS_X: {
                for (int i = 0; i < directions.length; i++) {
                    Direction dir = directions[i];
                    if (dir.isYAxis()) continue;
                    directions[i] = dir.getLeft();
                }
                break;
            }
            case NEG_X: {
                for (int i = 0; i < directions.length; i++) {
                    Direction dir = directions[i];
                    if (dir.isYAxis()) continue;
                    directions[i] = dir.getRight();
                }
                break;
            }
            case POS_Y: {
                for (int i = 0; i < directions.length; i++) {
                    Direction dir = directions[i];
                    if (dir.isXAxis()) continue;
                    switch (dir) {
                        case POS_Z: directions[i] = Direction.POS_Y; break;
                        case NEG_Z: directions[i] = Direction.NEG_Y; break;
                        case POS_Y: directions[i] = Direction.NEG_Z; break;
                        case NEG_Y: directions[i] = Direction.POS_Z; break;
                    }
                }
                break;
            }
            case NEG_Y: {
                for (int i = 0; i < directions.length; i++) {
                    Direction dir = directions[i];
                    if (dir.isXAxis()) continue;
                    switch (dir) {
                        case POS_Z: directions[i] = Direction.NEG_Y; break;
                        case NEG_Z: directions[i] = Direction.POS_Y; break;
                        case POS_Y: directions[i] = Direction.POS_Z; break;
                        case NEG_Y: directions[i] = Direction.NEG_Z; break;
                    }
                }
                break;
            }
        }

    }

    public LaserPulserBE() {}

    public LaserPulserBE(BlockState state, Zone zone, int gX, int gY, int gZ) {
        super(state, zone, gX, gY, gZ);
        String cond = getBlockEntityParamString(
                getBlockState().getBlock(),
                "pulseCondition"
        );
        if (cond == null) {
            condition = PulseCondition.BOTH;
        } else {
            condition = PulseCondition.valueOf(cond.toUpperCase(Locale.ROOT));
        }
    }

    @Override
    public void initBE() {
        addSignal("pulse", this::pulse);
        super.initBE();
    }

    @Override
    public Direction[] getPorts() {
        initPorts();
        return inPorts;
    }

    @Override
    public boolean canConnect(BlockState state, BlockState target, BlockEntity beTarget, Direction direction) {
        initPorts();
        if (beTarget instanceof LaserPulserBE) return false;
        if (Arrays.binarySearch(inPorts, direction) < 0) return false;

        IGameTagList list = target.getTags();
        return (list != null && list.contains(HorizonTags.TAG_CABLE_CONNECTABLE)) || beTarget instanceof IEnergyBE;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    private final BlockEventArgs args = new BlockEventArgs();
    private final BlockPosition position = new BlockPosition();

    private void pulse() {
        initPorts();
        args.zone = getZone();

        for (Direction port : outPorts) {
            int gX = getGlobalX() + port.getXOffset();
            int gY = getGlobalY() + port.getYOffset();
            int gZ = getGlobalZ() + port.getZOffset();

            BlockState state = getZone().getBlockState(gX, gY, gZ);

            args.srcBlockState = state;
            args.blockPos = position
                    .setGlobal(getZone(), getGlobalX(), getGlobalY(), getGlobalZ())
                    .getOffsetBlockPos(getZone(), port)
                    .copy();

            BlockEventTrigger[] triggers = state.getTrigger("onLaserHit");
            if (triggers == null) continue;
            args.run(triggers);
            args.runScheduledTriggers();
        }
    }

    private void pulse(boolean turnedOn) {
        switch (condition) {
            case BOTH -> pulse();
            case OFF -> { if (!turnedOn) pulse(); }
            case ON -> { if (turnedOn) pulse(); }
        }
    }

    @Override
    public void doTurnOn(Direction direction) {
        super.doTurnOn(direction);
        pulse(true);
    }

    @Override
    public void doTurnOff(Direction direction) {
        super.doTurnOff(direction);
        pulse(false);
    }

    public void initPorts() {
        Direction currentFacing = getBlockState().getParamDirection("direction");
        rotatePorts = getBlockEntityParam(getBlockState().getBlock(), "rotatePorts");
        if (currentFacing != this.facingDirection) {
            this.facingDirection = currentFacing;
            inPorts = null;
            outPorts = null;
        }
        if (inPorts == null) {
            Array<String> directions = getBlockEntityParam(getBlockState().getBlock(), "inPorts");
            this.inPorts = new Direction[directions.size];
            for (int i = 0; i < directions.size; i++) {
                String direction = directions.get(i);
                try {
                    this.inPorts[i] = Direction.fromStr(direction);
                } catch (Exception e) {
                    this.inPorts[i] = Direction.valueOf(direction.toUpperCase());
                }
            }
            rotatePorts(inPorts);
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
            rotatePorts(outPorts);
        }
    }

    @Override
    public String getBlockEntityId() {
        return ID;
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        serial.writeShort("pulseCondition", (short) condition.ordinal());
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        condition = PulseCondition.VALUES[deserial.readShort("pulseCondition", (short) 0)];
    }

}