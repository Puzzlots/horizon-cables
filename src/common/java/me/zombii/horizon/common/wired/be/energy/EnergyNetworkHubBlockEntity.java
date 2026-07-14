package me.zombii.horizon.common.wired.be.energy;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.wired.network.NetworkGroups;
import me.zombii.horizon.common.wired.network.NetworkManager;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyHubBlockEntity;

public class EnergyNetworkHubBlockEntity extends BlockEntity implements IEnergyHubBlockEntity {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "energy-network-hub");

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                ID.toString(),
                (block, zone, x, y, z) ->
                new EnergyNetworkHubBlockEntity(zone, x, y, z)
        );
    }

    private int networkID = -1;
    private EnergyNetwork network;

    public EnergyNetworkHubBlockEntity(Zone zone, int x, int y, int z) {
        super(zone, x, y, z);
    }

    @Override
    public void onInteract(Player player, Zone zone) {
        if (!GameSingletons.isClient()) return;
        Chat.MAIN_CLIENT_CHAT.addMessage(null, "Hub at (" + getGlobalX() + ", " + getGlobalY() + ", " + getGlobalZ() + ") has the network ID of " + networkID);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!GameSingletons.isHost()) return;

        Chunk chunk = getZone().getChunkAtBlock(getGlobalX(), getGlobalY(), getGlobalZ());

        if (networkID != -1) {
            network = NetworkGroups.energyNetworkGroup.get(networkID);
            return;
        }

        if (chunk != null) {
            network = NetworkGroups.energyNetworkGroup.newNetwork();
            networkID = network.getNetworkID();
            NetworkManager.build(network, BlockPosition.of(this), false);
        } else {
            Chat.MAIN_CLIENT_CHAT.addMessage(null, "Invalid hub block at (" + getGlobalX() + ", " + getGlobalY() + ", " + getGlobalZ() + "), replace me!");
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();

        if (!GameSingletons.isHost()) return;

        if (networkID == -1) return;
        if (NetworkGroups.energyNetworkGroup.get(networkID) == null) return;

        NetworkGroups.energyNetworkGroup.remove(networkID);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);

        this.networkID = deserial.readInt("networkID", -1);
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);

        serial.writeInt("networkID", networkID);
    }

    @Override
    public String getBlockEntityId() {
        return ID.toString();
    }

    @Override
    public EnergyNetwork getNetwork() {
        return network;
    }

}
