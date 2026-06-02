package me.zombii.horizon.common.network.power;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;

public interface IPowerBlock extends INetworkedBlock<PowerNetwork> {

    @Override
    default AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        return new PowerCableNode(network, pos, state, this);
    }

    @Override
    default AbstractNode createEmptyNode() {
        return new PowerCableNode();
    }

}
