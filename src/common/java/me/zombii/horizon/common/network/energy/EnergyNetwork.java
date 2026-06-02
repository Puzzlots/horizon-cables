package me.zombii.horizon.common.network.energy;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;
import me.zombii.horizon.common.network.energy.interfaces.IEnergyBlock;

public class EnergyNetwork extends AbstractNetwork {

    public EnergyNetwork() {
        super(IEnergyBlock.class);
    }

    @Override
    protected AbstractNode createNode(IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        return block.createNode(this, pos, state);
    }

}
