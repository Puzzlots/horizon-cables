package me.zombii.horizon.common.network;

import dev.puzzleshq.puzzleloader.cosmic.core.registries.GenericRegistry;
import dev.puzzleshq.puzzleloader.cosmic.core.registries.IRegistry;
import dev.puzzleshq.puzzleloader.cosmic.core.registries.MapRegistry;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.network.dcpower.DCPowerNetwork;
import me.zombii.horizon.common.network.energy.EnergyNetwork;

import java.util.ArrayList;
import java.util.List;

public class NetworkGroups {

    public static final IRegistry<NetworkGroup<?>> GROUP_REGISTRY = new GenericRegistry<>(Identifier.of(HorizonCommon.NAMESPACE, "NETWORK_GROUPS"));

    public static final NetworkGroup<DCPowerNetwork> dcPowerNetworkGroup = new NetworkGroup<>(DCPowerNetwork::new);
    public static final NetworkGroup<EnergyNetwork> energyNetworkGroup = new NetworkGroup<>(EnergyNetwork::new);

    static {
        GROUP_REGISTRY.store(Identifier.of(HorizonCommon.NAMESPACE, "DC_POWER"), dcPowerNetworkGroup);
        GROUP_REGISTRY.store(Identifier.of(HorizonCommon.NAMESPACE, "ENERGY"), energyNetworkGroup);
    }

}
