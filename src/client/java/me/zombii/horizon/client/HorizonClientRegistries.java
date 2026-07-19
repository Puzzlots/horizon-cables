package me.zombii.horizon.client;

import dev.puzzleshq.puzzleloader.cosmic.core.registries.GenericRegistry;
import dev.puzzleshq.puzzleloader.cosmic.core.registries.IRegistry;
import finalforeach.cosmicreach.ui.screens.BaseScreen;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.client.screen.HorizonBaseScreen;
import me.zombii.horizon.client.screen.HorizonGameState;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

import java.util.function.Function;

public class HorizonClientRegistries {

    public static final IRegistry<Function<ScreenOpenInfo, HorizonBaseScreen>> SCREEN_REGISTRY = new GenericRegistry<>(Identifier.of(HorizonCommon.NAMESPACE, "SCREENS"));
    public static final IRegistry<Function<ScreenOpenInfo, HorizonGameState>> GAMESTATE_REGISTRY = new GenericRegistry<>(Identifier.of(HorizonCommon.NAMESPACE, "GAMESTATES"));

}
