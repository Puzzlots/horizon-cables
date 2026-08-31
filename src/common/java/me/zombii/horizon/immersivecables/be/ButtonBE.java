package me.zombii.horizon.immersivecables.be;

import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.AbstractEnergyBE;
import me.zombii.horizon.immersivecables.IEnergyBE;
import me.zombii.horizon.immersivecables.ImEventManager;

public class ButtonBE extends AbstractEnergyBE {

    public static final String ID = "horizon:button-block-entity";

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(ID, ButtonBE::new);
    }

    private Direction[] ports;

    public ButtonBE() {}

    public ButtonBE(BlockState state, Zone zone, int gX, int gY, int gZ) {
        super(state, zone, gX, gY, gZ);
    }

    @Override
    public void initBE() {
        addSignal("toggle", () -> onInteract(null, getZone()));
        super.initBE();
    }

    @Override
    public Direction[] getPorts() {
        return ports;
    }

    @Override
    public boolean canConnect(BlockState state, BlockState target, BlockEntity beTarget, Direction direction) {
        if (beTarget instanceof ButtonBE) return false;
        initPorts();

        IGameTagList list = target.getTags();
        return (list != null && list.contains(HorizonTags.TAG_CABLE_CONNECTABLE)) || beTarget instanceof IEnergyBE;
    }

    @Override
    public void doTurnOff(Direction direction) {
    }

    @Override
    public void doTurnOn(Direction direction) {
    }

    private final BlockEventArgs eventArgs = new BlockEventArgs();

    public void initPorts() {
        if (ports == null) {
            Array<String> directions = getBlockEntityParam(getBlockState().getBlock(), "ports");
            this.ports = new Direction[directions.size];
            for (int i = 0; i < directions.size; i++) {
                String direction = directions.get(i);
                try {
                    this.ports[i] = Direction.fromStr(direction);
                } catch (Exception e) {
                    this.ports[i] = Direction.valueOf(direction.toUpperCase());
                }
            }
        }
    }

    private int remainingTicks = 0;

    @Override
    public void onTick() {
        super.onTick();
        if (remainingTicks > 0) {
            remainingTicks--;
            return;
        }
        initPorts();

        BlockEventTrigger[] events;
        if (isOn) {
            events = getBlockState().getTrigger("onTurnOn");
        } else {
            events = getBlockState().getTrigger("onTurnOff");
        }

        if (events != null) {
            eventArgs.blockPos = getBlockPosition();
            eventArgs.zone = getZone();
            eventArgs.srcBlockState = getBlockState();
            eventArgs.run(events);
            eventArgs.runScheduledTriggers();
        }

        for (Direction port : getPorts()) {
            if (canConnect(port)) {
                int gX = getGlobalX() + port.getXOffset();
                int gY = getGlobalY() + port.getYOffset();
                int gZ = getGlobalZ() + port.getZOffset();

                BlockEntity entity = getZone().getBlockEntity(gX, gY, gZ);
                if (!(entity instanceof IEnergyBE energyBE)) continue;

                ImEventManager.queueEvent(getZone(), gX, gY, gZ, isOn, port);
//                if (isOn()) {
//                    energyBE.turnOn(port);
//                } else {
//                    energyBE.turnOff(port);
//                }
            }
        }
        if (isOn) {
            remainingTicks = 30;
            isOn = false;
            setTicking(true);
        } else {
            remainingTicks = 0;
            setTicking(false);
        }
    }

    @Override
    public void onInteract(Player player, Zone zone) {
        if (!GameSingletons.isHost()) return;
        initPorts();

        isOn = true;
        remainingTicks = 0;

        setTicking(true);
    }

    @Override
    public String getBlockEntityId() {
        return ID;
    }

    @Override
    public void write(CRBinSerializer serial) {
        serial.writeInt("remainingTicks", remainingTicks);
        super.write(serial);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        remainingTicks = deserial.readInt("remainingTicks", 0);
        super.read(deserial);
    }

}