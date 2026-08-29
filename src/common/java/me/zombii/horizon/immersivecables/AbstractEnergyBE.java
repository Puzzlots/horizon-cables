package me.zombii.horizon.immersivecables;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEvents;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
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

    @Override
    public BlockPosition getBlockPosition() {
        return BlockPosition.ofGlobal(getZone(), getGlobalX(), getGlobalY(), getGlobalZ());
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        isOn = deserial.readBoolean("isOn", false);
    }
}
