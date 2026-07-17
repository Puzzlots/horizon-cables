package me.zombii.horizon.common.wired.network;

import dev.puzzleshq.puzzleloader.cosmic.core.registries.GenericRegistry;
import dev.puzzleshq.puzzleloader.cosmic.core.registries.IRegistry;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;

public class NetworkGroups {

    public static final IRegistry<NetworkGroup<?>> GROUP_REGISTRY = new GenericRegistry<>(Identifier.of(HorizonCommon.NAMESPACE, "NETWORK_GROUPS"));

    public static final NetworkGroup<EnergyNetwork> energyNetworkGroup = new NetworkGroup<>(EnergyNetwork::new);

    static {
        GROUP_REGISTRY.store(Identifier.of(HorizonCommon.NAMESPACE, "ENERGY"), energyNetworkGroup);
    }

}
