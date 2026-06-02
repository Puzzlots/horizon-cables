package me.zombii.horizon.common.network.dcpower;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;

public class DCPowerNetwork extends AbstractNetwork {

    public DCPowerNetwork() {
        super(IDCPowerBlock.class);
    }

    @Override
    protected AbstractNode createNode(IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        return block.createNode(this, pos, state);
    }

}
