package me.zombii.horizon.common.wired.network.energy.interfaces;

import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.IBlockEntity;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;

import java.util.function.BiFunction;

public interface IEnergyHubBlockEntity {

    BiFunction<IEnergyBlock, IReadBlockPosition, EnergyNetwork> NETWORK_DISCOVERY_FUNCTION
            = (block, blockPosition) -> {
        IBlockEntity blockEntity = blockPosition.getBlockEntity();

        if (blockEntity instanceof IEnergyHubBlockEntity hub) {
            return hub.getNetwork();
        }
        return null;
    };

    EnergyNetwork getNetwork();

}
