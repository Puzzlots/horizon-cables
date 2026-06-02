package me.zombii.horizon.common.network.dcpower;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;

public interface IDCPowerBlock extends INetworkedBlock<DCPowerNetwork> {

    @Override
    default AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        return new DCPowerCableNode(network, pos, state, this);
    }

    @Override
    default AbstractNode createEmptyNode() {
        return new DCPowerCableNode();
    }

}
