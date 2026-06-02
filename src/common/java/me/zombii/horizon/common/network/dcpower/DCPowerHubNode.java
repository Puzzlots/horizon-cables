package me.zombii.horizon.common.network.dcpower;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.INetworkedBlock;

public class DCPowerHubNode extends DCPowerCableNode {

    public DCPowerHubNode() {
        super();
    }

    public DCPowerHubNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

}
