package me.zombii.horizon.common.wired.be.energy;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.MissingBlockStateResult;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.BlockSetter;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.wired.network.NetworkGroups;
import me.zombii.horizon.common.wired.network.NetworkManager;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyHubBlockEntity;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeSwitch;

public class SwitchBlockEntity extends BlockEntity implements IEnergyHubBlockEntity {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "switch");

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                ID.toString(),
                (block, zone, x, y, z) ->
                new SwitchBlockEntity(zone, x, y, z)
        );
    }

    private int networkID = -1;
    private EnergyNetwork network;

    public SwitchBlockEntity(Zone zone, int x, int y, int z) {
        super(zone, x, y, z);
    }

    @Override
    public void onInteract(Player player, Zone zone) {
        if (!GameSingletons.isHost()) return;

        String message = "Switch Hub at (" + getGlobalX() + ", " + getGlobalY() + ", " + getGlobalZ() + ") has the network ID of " + networkID;

        if (player.getEntity().viewPositionOffset == player.sneakingViewPositionOffset) {
            if (!GameSingletons.isClient()) {
                ServerSingletons.getConnection(player).sendChatMessage(message);
            } else {
                Chat.MAIN_CLIENT_CHAT.addMessage(null, message);
            }
        }

        EnergyNodeSwitch node = (EnergyNodeSwitch) network.getNode(getGlobalX(), getGlobalY(), getGlobalZ());

        node.toggle();
        BlockState state = BlockState.getInstance("horizon:switch[" + (node.isPowered() ? "on" : "default") + "]", MissingBlockStateResult.MISSING_OBJECT);
        node.setState(state);
        BlockSetter.get().replaceBlock(zone, state, getGlobalX(), getGlobalY(), getGlobalZ());
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
            Chat.MAIN_CLIENT_CHAT.addMessage(null, "Invalid switch hub block at (" + getGlobalX() + ", " + getGlobalY() + ", " + getGlobalZ() + "), replace me!");
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
