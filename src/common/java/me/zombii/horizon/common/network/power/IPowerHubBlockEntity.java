package me.zombii.horizon.common.network.power;

import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.IBlockEntity;

import java.util.function.BiFunction;

public interface IPowerHubBlockEntity {

    BiFunction<IPowerBlock, IReadBlockPosition, PowerNetwork> NETWORK_DISCOVERY_FUNCTION
            = (block, blockPosition) -> {
        IBlockEntity blockEntity = blockPosition.getBlockEntity();

        if (blockEntity instanceof IPowerHubBlockEntity hub) {
            return hub.getPowerNetwork();
        }
        return null;
    };

    PowerNetwork getPowerNetwork();

}
