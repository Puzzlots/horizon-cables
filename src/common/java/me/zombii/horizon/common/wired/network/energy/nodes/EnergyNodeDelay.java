package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EnergyNodeDelay extends EnergyNode {

    public EnergyNodeDelay() {
        super();
    }

    public EnergyNodeDelay(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    AtomicBoolean isWaiting = new AtomicBoolean(false);
    AtomicBoolean shouldBeOn = new AtomicBoolean(false);

    @Override
    public void powerOn(Direction direction) {
        if (isWaiting.get()) {
            shouldBeOn.set(true);
            return;
        }
        shouldBeOn.set(true);
        super.powerOn(direction);
    }

    @Override
    public void powerOff(Direction direction) {
        if (isWaiting.get()) {
            shouldBeOn.set(false);
            return;
        }
        shouldBeOn.set(false);
        super.powerOff(direction);
    }

    public static final int delayTicks = 5;

    @Override
    public void onPowerOn(Direction direction) {
        Direction pointing = getState().getParamDirection("direction");
        if (direction != pointing) return;

        AbstractNode node = getNetwork().getNode(
                getX() + direction.getXOffset(),
                getY() + direction.getYOffset(),
                getZ() + direction.getZOffset()
        );
        AbstractNode bnode = getNetwork().getNode(
                getX() - direction.getXOffset(),
                getY() - direction.getYOffset(),
                getZ() - direction.getZOffset()
        );
        if (!(node instanceof EnergyNode eNode)) return;
        if (!(bnode instanceof EnergyNode beNode)) return;
        long lastTick = GameSingletons.world.currentWorldTick + delayTicks;
        AtomicReference<Runnable> waitings = new AtomicReference<>();
        isWaiting.set(true);
        waitings.set(() -> {
            if (lastTick >= GameSingletons.world.currentWorldTick) {
                isWaiting.set(true);
                push(waitings.get());
                return;
            }
            isWaiting.set(false);
            eNode.powerOn(direction);
            if (!beNode.isPowered()) powerOff(direction);
            push(() -> {
                if (!shouldBeOn.get()) {
                    powerOff(direction);
                }
            });
        });
        push(waitings.get());
    }

    @Override
    public void onPowerOff(Direction direction) {
        Direction pointing = getState().getParamDirection("direction");
        if (direction != pointing) return;

        AbstractNode node = getNetwork().getNode(
                getX() + direction.getXOffset(),
                getY() + direction.getYOffset(),
                getZ() + direction.getZOffset()
        );
        if (!(node instanceof EnergyNode eNode)) return;
        long lastTick = GameSingletons.world.currentWorldTick + delayTicks;
        AtomicReference<Runnable> waitings = new AtomicReference<>();
        isWaiting.set(true);
        waitings.set(() -> {
            if (lastTick >= GameSingletons.world.currentWorldTick) {
                isWaiting.set(true);
                push(waitings.get());
                return;
            }
            isWaiting.set(false);
            eNode.powerOff(direction);
            push(() -> {
                if (shouldBeOn.get()) {
                    powerOn(direction);
                }
            });
        });
        push(waitings.get());
    }
    
}
