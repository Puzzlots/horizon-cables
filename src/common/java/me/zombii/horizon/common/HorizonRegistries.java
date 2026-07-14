package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.core.registries.GenericRegistry;
import dev.puzzleshq.puzzleloader.cosmic.core.registries.IRegistry;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.wired.network.NetworkGroup;
import me.zombii.horizon.common.wired.network.NetworkGroups;

import java.util.function.Supplier;

public class HorizonRegistries {

    public static final IRegistry<NetworkGroup<?>> NETWORK_GROUP_REGISTRY = NetworkGroups.GROUP_REGISTRY;
    public static final IRegistry<Supplier<AbstractDataStorageDevice>> PC_COMPONENT_REGISTRY = new GenericRegistry<>(Identifier.of(HorizonCommon.NAMESPACE, "PC_COMPONENTS"));

}
