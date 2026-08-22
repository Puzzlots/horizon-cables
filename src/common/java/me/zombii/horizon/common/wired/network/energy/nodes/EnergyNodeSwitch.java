package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;
import me.zombii.horizon.common.wired.network.NodeReference;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class EnergyNodeSwitch extends EnergyNode {

    public EnergyNodeSwitch() {
        super();
    }

    public EnergyNodeSwitch(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    public void toggle() {
        if (!GameSingletons.isHost()) return;
        setPowered(!isPowered());

        spreadPower();
    }

    @Override
    public void powerOn(Direction direction) {
    }

    @Override
    public void powerOff(Direction direction) {
    }

    private void spreadPower() {
        for (int i = 0; i < getConnections().length; i++) {
            NodeReference connection = getConnections()[i];
            if (connection == null || connection.getNetwork() == null) continue;
            Direction direction = Direction.ALL_DIRECTIONS[i];
            AbstractNode node = connection.getNode();
            if (node instanceof EnergyNode eNode) {
//                if (isPowered()) eNode.powerOn(direction);
//                else eNode.powerOff(direction);
                if (isPowered()) push(() -> eNode.powerOn(direction));
                else push(() -> eNode.powerOff(direction));
            }
        }
    }

}
