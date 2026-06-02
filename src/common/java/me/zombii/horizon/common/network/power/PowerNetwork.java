package me.zombii.horizon.common.network.power;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;

public class PowerNetwork extends AbstractNetwork {

    public PowerNetwork() {
        super(IPowerBlock.class);
    }

    @Override
    protected AbstractNode createNode(IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        return block.createNode(this, pos, state);
    }

}
