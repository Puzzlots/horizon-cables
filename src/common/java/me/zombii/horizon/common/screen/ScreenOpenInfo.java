package me.zombii.horizon.common.screen;

import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.util.Identifier;

public record ScreenOpenInfo(
        Player player,
        Identifier screenId,
        IReadBlockPosition position,
        ItemStack stack,
        int windowId,
        boolean isGameState
) {}
