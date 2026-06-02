package me.zombii.horizon.common.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;

public class EnergyCableNode extends AbstractNode {

    public EnergyCableNode() {
        super();
    }

    public EnergyCableNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

}
