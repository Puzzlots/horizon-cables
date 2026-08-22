package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;

public class EnergyNodeInverter extends EnergyNode {

    public EnergyNodeInverter() {
        super();
    }

    public EnergyNodeInverter(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    @Override
    public void onPowerOn(Direction direction) {
        Direction pointing = getState().getParamDirection("direction");
        if (direction != pointing) return;

        AbstractNode node = getNetwork().getNode(
                getX() + direction.getXOffset(),
                getY() + direction.getYOffset(),
                getZ() + direction.getZOffset()
        );
        if (!(node instanceof EnergyNode eNode)) return;
//        push(() -> eNode.powerOff(direction));
        eNode.powerOff(direction);
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
        eNode.powerOn(direction);
//        push(() -> eNode.powerOn(direction));
    }

}
