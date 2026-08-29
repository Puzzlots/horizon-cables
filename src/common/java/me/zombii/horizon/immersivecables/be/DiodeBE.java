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

import java.util.Arrays;

public class DiodeBE extends AbstractEnergyBE {

    public static final String ID = "horizon:diode-block-entity";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, DiodeBE::new);
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

    public DiodeBE() {}

    public DiodeBE(BlockState state, Zone zone, int gX, int gY, int gZ) {
        super(state, zone, gX, gY, gZ);
        Boolean invOut = getBlockEntityParam(getBlockState().getBlock(), "invertOutputs");
        invertOutputs = invOut != null && invOut;
        Boolean invInp = getBlockEntityParam(getBlockState().getBlock(), "invertInputs");
        invertInputs = invInp != null && invInp;
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
        if ((Arrays.binarySearch(inPorts, direction) < 0 && Arrays.binarySearch(outPorts, direction) < 0)) return false;

        IGameTagList list = target.getTags();
        return (list != null && list.contains(HorizonTags.TAG_CABLE_CONNECTABLE)) || beTarget instanceof IEnergyBE;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    private final BlockEventArgs args = new BlockEventArgs();

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

    @Override
    public void doTurnOn(Direction direction) {
        if (Arrays.binarySearch(inPorts, direction.getOpposite()) < 0) return;
        isOn = !this.invertInputs;
        triggerEvents();
        if (remainingTicks != 0) return;
        if (delay != 0) scheduleToggle(isOn);
        else sendPowerOut(isOn);
    }

    @Override
    public void doTurnOff(Direction direction) {
        if (Arrays.binarySearch(inPorts, direction.getOpposite()) < 0) return;
        isOn = this.invertInputs;
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
        serial.writeBoolean("invertOutputs", invertOutputs);
        serial.writeBoolean("invertInputs", invertInputs);
        serial.writeInt("remainingTicks", remainingTicks);
        serial.writeInt("delay", delay);
        super.write(serial);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        invertOutputs = deserial.readBoolean("invertOutputs", false);
        invertInputs = deserial.readBoolean("invertInputs", false);
        remainingTicks = deserial.readInt("remainingTicks", remainingTicks);
        delay = deserial.readInt("delay", delay);
        super.read(deserial);
    }

}