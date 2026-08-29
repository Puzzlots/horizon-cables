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
import me.zombii.horizon.immersivecables.LogicGate;
import me.zombii.horizon.immersivecables.PulseCondition;

import java.util.Arrays;
import java.util.Locale;

public class LogicGateBE extends AbstractEnergyBE {

    public static final String ID = "horizon:logic-gate-block-entity";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, LogicGateBE::new);
    }

    private Direction[] inPorts;
    private Direction[] outPorts;
    private boolean rotatePorts;
    private Direction facingDirection;
    private boolean invertOutputs;
    private boolean invertInputs;
    private int delay;
    private int remainingTicks;
    private boolean scheduledPower;
    private LogicGate gateType;

    public LogicGateBE() {}

    public LogicGateBE(BlockState state, Zone zone, int gX, int gY, int gZ) {
        super(state, zone, gX, gY, gZ);
        Boolean invOut = getBlockEntityParam(getBlockState().getBlock(), "invertOutputs");
        invertOutputs = invOut != null && invOut;
        Boolean invInp = getBlockEntityParam(getBlockState().getBlock(), "invertInputs");
        invertInputs = invInp != null && invInp;

        String gType = getBlockEntityParamString(
                getBlockState().getBlock(),
                "gateType"
        );
        if (gType == null) {
            gateType = LogicGate.OR;
        } else {
            gateType = LogicGate.valueOf(gType.toUpperCase(Locale.ROOT));
        }
    }

    private void scheduleToggle(boolean power) {
        this.remainingTicks = delay;
        this.scheduledPower = power;
        setTicking(true);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (remainingTicks > 0) {
            remainingTicks--;
            return;
        }
        sendPowerOut(scheduledPower);
        if (isOn == scheduledPower) {
            setTicking(false);
        } else scheduleToggle(isOn);
        remainingTicks = 0;
    }

    @Override
    public void initBE() {
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
        boolean isInput = Arrays.stream(inPorts).anyMatch(i -> i == direction);
        boolean isOutput = Arrays.stream(outPorts).anyMatch(i -> i == direction);
        if (!isInput && !isOutput) return false;

        IGameTagList list = target.getTags();
        return (list != null && list.contains(HorizonTags.TAG_CABLE_CONNECTABLE)) || beTarget instanceof IEnergyBE;
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
                    if (isOn) ebe.turnOn(port);
                    else ebe.turnOff(port);
                }
            }
        }
    }

    private boolean isOn(Direction port) {
        if (canConnect(port)) {
            int gX = getGlobalX() + port.getXOffset();
            int gY = getGlobalY() + port.getYOffset();
            int gZ = getGlobalZ() + port.getZOffset();

            BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);

            if (entity instanceof IEnergyBE ebe) {
                return ebe.isOn();
            }
        }
        return false;
    }

    private boolean isGateTrue() {
        return switch (gateType) {
            case OR -> isOrTrue();
            case AND -> isANDTrue();
            case XOR -> isXORTrue();
        };
    }

    private boolean isANDTrue() {
        boolean previous = false;
        boolean first = false;
        for (Direction port : inPorts) {
            boolean portOn = isOn(port) == !this.invertInputs;
            if (!first) {
                previous = portOn;
                first = true;
            }
            else previous = portOn && previous;
        }
        return previous;
    }

    private boolean isXORTrue() {
        boolean previous = false;
        boolean first = false;
        for (Direction port : inPorts) {
            boolean portOn = isOn(port) == !this.invertInputs;
            if (!first) {
                previous = portOn;
                first = true;
            }
            else previous = previous ^ (portOn);
        }
        return previous;
    }

    private boolean isOrTrue() {
        boolean previous = false;
        boolean first = false;
        for (Direction port : inPorts) {
            boolean portOn = isOn(port) == !this.invertInputs;
            if (!first) {
                previous = portOn;
                first = true;
            }
            else previous = previous || (portOn);
        }
        return previous;
    }

//    @Override
//    public void turnOn(Direction direction) {
//        doTurnOn(direction);
//    }
//
//    @Override
//    public void turnOff(Direction direction) {
//        doTurnOff(direction);
//    }

    @Override
    public void doTurnOn(Direction direction) {
        if (Arrays.stream(inPorts).noneMatch(i -> i == direction.getOpposite())) return;

        isOn = isGateTrue();
        triggerEvents();
        if (remainingTicks != 0) return;
        if (delay != 0) scheduleToggle(isOn);
        else sendPowerOut(isOn);
    }

    @Override
    public void doTurnOff(Direction direction) {
        if (Arrays.stream(inPorts).noneMatch(i -> i == direction.getOpposite())) return;

        isOn = !isGateTrue();
        triggerEvents();
        if (remainingTicks != 0) return;
        if (delay != 0) scheduleToggle(isOn);
        else sendPowerOut(isOn);
    }

    public void initPorts() {
        Direction currentFacing = getBlockState().getParamDirection("direction");
        rotatePorts = getBlockEntityParam(getBlockState().getBlock(), "rotatePorts");
        delay = getBlockEntityParamInt(getBlockState().getBlock(), "delay", 0);
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
            rotatePorts(rotatePorts, facingDirection, inPorts);
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
        serial.writeBoolean("invertInputs", invertInputs);
        serial.writeInt("remainingTicks", remainingTicks);
        serial.writeInt("delay", delay);
        serial.writeShort("gateType", (short) gateType.ordinal());
        super.write(serial);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        invertOutputs = deserial.readBoolean("invertOutputs", false);
        invertInputs = deserial.readBoolean("invertInputs", false);
        remainingTicks = deserial.readInt("remainingTicks", remainingTicks);
        delay = deserial.readInt("delay", delay);
        gateType = LogicGate.VALUES[deserial.readShort("gateType", (short) 0)];
        super.read(deserial);
    }

}