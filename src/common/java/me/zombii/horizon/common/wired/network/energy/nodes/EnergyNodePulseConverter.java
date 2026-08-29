package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;

import java.util.Arrays;

public class EnergyNodePulseConverter extends EnergyNode {

    public EnergyNodePulseConverter() {
        super();
    }

    public EnergyNodePulseConverter(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    private final BlockEventArgs args = new BlockEventArgs();

    private void pulse() {
        Direction direction = getState().getParamDirection("direction");
        BlockPosition position = BlockPosition.ofGlobal(
                getZone(),
                getX() + direction.getXOffset(),
                getY() + direction.getYOffset(),
                getZ() + direction.getZOffset()
        );
        if (position.chunk == null) return;
        BlockState state = position.getBlockState();

        args.blockPos = position;
        args.srcBlockState = state;
        args.zone = getZone();
        BlockEventTrigger[] triggers = state.getTrigger("onLaserHit");
        if (triggers == null) return;
        for (BlockEventTrigger onLaserHit : triggers) {
            if (onLaserHit == null) continue;
            onLaserHit.act(args);
        }
    }

    @Override
    public void onPowerOn(Direction direction) {
        pulse();
    }

    @Override
    public void onPowerOff(Direction direction) {
        pulse();
    }
}
