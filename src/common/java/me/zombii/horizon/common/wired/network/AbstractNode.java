package me.zombii.horizon.common.wired.network;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.BlockLoader;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.blocks.MissingBlockStateResult;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.Arrays;

public abstract class AbstractNode {

    public static final int NEG_X = 0;
    public static final int POS_X = 1;
    public static final int NEG_Y = 2;
    public static final int POS_Y = 3;
    public static final int NEG_Z = 4;
    public static final int POS_Z = 5;

    private final NodeReference[] connections;
    private AbstractNetwork network;
    private BlockState state;
    private INetworkedBlock<?> block;
    private NodeReference self;
    private Zone zone;
    private String zoneId;

    private int x, y, z;

    public AbstractNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        this.connections = new NodeReference[6];
        this.network = network;
        this.state = state;
        this.block = block;

        this.zone = pos.getZone();
        this.zoneId = zone.zoneId;

        this.x = pos.getGlobalX();
        this.y = pos.getGlobalY();
        this.z = pos.getGlobalZ();

        this.self = new NodeReference(network.getGroup(), network.getNetworkID(), x, y, z);
    }

    protected AbstractNode() {
        this.connections = new NodeReference[6];
        this.network = null;
        this.state = null;
        this.block = null;
        this.zone = null;

        this.x = 0;
        this.y = 0;
        this.z = 0;

        this.self = null;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public Zone getZone() {
        if (zone == null) {
            zone = GameSingletons.world.getZoneIfExists(
                    zoneId == null ? GameSingletons.world.defaultZoneId : zoneId
            );
        }
        return zone;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public NodeReference[] getConnections() {
        return connections;
    }

    public AbstractNetwork getNetwork() {
        return network;
    }

    public void resetConnections() {
        Arrays.fill(connections, null);
    }

    public BlockState getState() {
        return state;
    }

    public INetworkedBlock<?> getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "Node: {State: " + state + " POS: [" + x + ", " + y + ", " + z + "]}";
    }

    public void save(JsonObject nodeObject) {
        // save node info
        nodeObject.add("position", new JsonArray()
                .add(this.getX())
                .add(this.getY())
                .add(this.getZ())
        );
        nodeObject.add("zone", getZone().zoneId);

        JsonArray connections = new JsonArray();
        for (NodeReference con : this.connections) {
            // saves connection info
            if (con == null) {
                connections.add(JsonValue.NULL);
                continue;
            }

            JsonObject connection = new JsonObject();
            connection.add("position", new JsonArray().add(con.x()).add(con.y()).add(con.z()));
            connection.add("networkID", con.networkID());
            connections.add(connection);
        }

        nodeObject.add("connections", connections);
        nodeObject.set("blockID", block.getId().toString());
        nodeObject.set("stateID", state.getSaveKey());
    }

    public void load(AbstractNetwork network, JsonObject nodeObject) {
        JsonArray positions = nodeObject.get("position").asArray();
        this.x = positions.get(0).asInt();
        this.y = positions.get(1).asInt();
        this.z = positions.get(2).asInt();
        this.zoneId = nodeObject.getString("zone", null);

        NetworkGroup<?> group = network.getGroup();

        JsonArray connections = nodeObject.get("connections").asArray();
        for (int i = 0; i < connections.size(); i++) {
            JsonValue connection = connections.get(i);
            if (connection == JsonValue.NULL) continue;
            JsonObject connectionObject = connection.asObject();
            JsonArray connectionPosition = connectionObject.get("position").asArray();
            int networkID = connectionObject.get("networkID").asInt();

            this.connections[i] = new NodeReference(
                    group, networkID,
                    connectionPosition.get(0).asInt(),
                    connectionPosition.get(1).asInt(),
                    connectionPosition.get(2).asInt()
            );

        }
        this.network = network;
        this.self = new NodeReference(group, network.getNetworkID(), x, y, z);
        this.block = (INetworkedBlock<?>) BlockLoader.INSTANCE.getModdedFromVanillaBlock(Block.getById(nodeObject.get("blockID").asString()));
        this.state = BlockState.getInstance(nodeObject.get("stateID").asString(), MissingBlockStateResult.MISSING_OBJECT);
    }

    public NodeReference getRef() {
        return self;
    }

}
