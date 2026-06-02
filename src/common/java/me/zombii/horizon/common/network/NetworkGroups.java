package me.zombii.horizon.common.network;

import me.zombii.horizon.common.network.power.PowerNetwork;

public class NetworkGroups {

    public static final NetworkGroup<PowerNetwork> powerNetworkGroup = new NetworkGroup<>(PowerNetwork::new);

}
