package me.zombii.horizon.common.network.power;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.INetworkedBlock;

public class PowerHubNode extends PowerCableNode {

    public PowerHubNode() {
        super();
    }

    public PowerHubNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

}
