package me.zombii.horizon.immersivecables;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEvents;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.DirectionUtil;

public abstract class AbstractEnergyBE extends BlockEntity implements IEnergyBE {

    protected boolean isOn = false;

    public AbstractEnergyBE() {}

    private BlockState state;

    public AbstractEnergyBE(BlockState state, Zone zone, int gX, int gY, int gZ) {
        super(zone, gX, gY, gZ);
        this.state = state;
        initBE();
    }

    @Override
    public BlockState getBlockState() {
        BlockState foundState = super.getBlockState();
        if (foundState == null) return state;
        return foundState;
    }

    public void initBE() {
        initPorts();
        for (Direction direction : Direction.ALL_DIRECTIONS) {
            addSignal("turnOn-" + DirectionUtil.toString(direction), () -> turnOn(direction));
            addSignal("turnOff-" + DirectionUtil.toString(direction), () -> turnOff(direction));
        }
    }

    public void triggerEvents() {
        BlockEvents events = getBlockState().getBlockEvents();
        BlockEventTrigger[] triggers = isOn
                ? events.getTriggers("onTurnOn")
                : events.getTriggers("onTurnOff");

        if (triggers == null) return;

        BlockEventArgs args = BlockEventArgs.POOL.obtain();
        args.run(triggers);
        args.runScheduledTriggers();
        BlockEventArgs.POOL.free(args);
    }

    public void doTurnOn(Direction direction) {
        isOn = true;
        triggerEvents();
    }

    @Override
    public void doTurnOff(Direction direction) {
        isOn = false;
        triggerEvents();
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    public BlockPosition getBlockPosition() {
        return BlockPosition.ofGlobal(getZone(), getGlobalX(), getGlobalY(), getGlobalZ());
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        isOn = deserial.readBoolean("isOn", false);
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        serial.writeBoolean("isOn", isOn);
    }

    /*
     * front  POS_Z
     * back   NEG_Z
     * top    POS_Y
     * bottom NEG_Y
     * right  POS_X
     * left   NEG_X
     */

    protected void rotatePorts(boolean rotatePorts, Direction facingDirection, Direction[] directions) {
        if (!rotatePorts) return;
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
}
