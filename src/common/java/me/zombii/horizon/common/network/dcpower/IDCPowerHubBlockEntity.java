package me.zombii.horizon.common.network.dcpower;

import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.IBlockEntity;

import java.util.function.BiFunction;

public interface IDCPowerHubBlockEntity {

    BiFunction<IDCPowerBlock, IReadBlockPosition, DCPowerNetwork> NETWORK_DISCOVERY_FUNCTION
            = (block, blockPosition) -> {
        IBlockEntity blockEntity = blockPosition.getBlockEntity();

        if (blockEntity instanceof IDCPowerHubBlockEntity hub) {
            return hub.getPowerNetwork();
        }
        return null;
    };

    DCPowerNetwork getPowerNetwork();

}
