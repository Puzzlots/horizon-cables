package me.zombii.horizon.common.wired.network;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.BlockLoader;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.util.IPoint3DMap;
import finalforeach.cosmicreach.util.Point3DMap;
import finalforeach.cosmicreach.util.constants.Direction;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractNetwork {

    private final IPoint3DMap<AbstractNode> nodeMap;
    private final List<AbstractNode> nodeList;
    private final AtomicInteger nodeCount;
    private final Class<?> networkClass;
    private final AtomicInteger netID;
    private NetworkGroup<?> group;

    public <T extends INetworkedBlock<?>> AbstractNetwork(
            Class<T> networkClass
    ) {
        this.nodeMap = new Point3DMap<>();
        this.nodeList = new LinkedList<>();
        this.nodeCount = new AtomicInteger(0);
        this.networkClass = networkClass;
        this.netID = new AtomicInteger(0);
    }

    public Class<?> getNetworkClass() {
        return networkClass;
    }

    public IPoint3DMap<AbstractNode> getNodeMap() {
        return nodeMap;
    }

    public List<AbstractNode> getNodeList() {
        return nodeList;
    }

    public int getNodeCount() {
        return nodeList.size();
    }

    public void setNetworkID(int netID) {
        this.netID.set(netID);
    }
    public int getNetworkID() {
        return netID.get();
    }

    public void clear() {
        nodeMap.clear();
        nodeList.clear();
        nodeCount.set(0);
    }

    public static AbstractNetwork returnLargest(AbstractNetwork network, AbstractNetwork... networks) {
        if (networks.length == 0) return network;
        AbstractNetwork assumedLargestNetwork = Arrays.stream(networks)
                .sorted((a, b) -> Integer.compare(b.getNodeCount(), a.getNodeCount()))
                .toArray(AbstractNetwork[]::new)[0];

        return assumedLargestNetwork.getNodeCount() > network.getNodeCount()
                ? assumedLargestNetwork
                : network;
    }

    protected abstract AbstractNode createNode(IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block);

    public AbstractNode getOrCreateNode(IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        AbstractNode existingNode = getNodeMap().get(pos.getGlobalX(),  pos.getGlobalY(), pos.getGlobalZ());
        if (
                existingNode == null
                || (!existingNode.getBlock().equals(BlockLoader.INSTANCE.getModdedFromVanillaBlock(state.getBlock())))
                || (!existingNode.getState().equals(state))
        ) {
            return newNode(pos, state, block);
        }
        return existingNode;
    }

    public AbstractNode newNode(IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        AbstractNode node = createNode(pos, state, block);
        this.nodeMap.put(node, pos.getGlobalX(), pos.getGlobalY(), pos.getGlobalZ());
        this.nodeList.add(node);
        this.nodeCount.getAndIncrement();
        return node;
    }

    public void removeNode(IReadBlockPosition pos) {
        removeNode(pos.getGlobalX(), pos.getGlobalY(), pos.getGlobalZ());
    }

    public void removeNode(int x, int y, int z) {
        AbstractNode node = nodeMap.get(x, y, z);
        if (node == null) return;

        nodeMap.remove(x, y, z);
        nodeList.remove(node);

        NodeReference[] connections = node.getConnections();
        for (int i = 0; i < connections.length; i++) {
            Direction direction = Direction.ALL_DIRECTIONS[i];
            Direction opposite = direction.getOpposite();

            NodeReference oldNodeRef = connections[i];
            if (oldNodeRef == null) continue;
            oldNodeRef.getNode().getConnections()[opposite.ordinal()] = null;
        }
    }

    public void save(JsonObject networkObject) {
        JsonArray nodes = new JsonArray();
        for (AbstractNode abstractNode : nodeList) {
            JsonObject nodeObject = new JsonObject();
            abstractNode.save(nodeObject);
            nodes.add(nodeObject);
        }
        networkObject.set("nodes", nodes);
        networkObject.set("netID", this.netID.get());
    }

    public void load(JsonObject networkObject) {
        JsonArray nodes = networkObject.get("nodes").asArray();
        for (JsonValue node : nodes) {
//            System.out.println(node);
            JsonObject nodeObject = node.asObject();
            INetworkedBlock<?> block = (INetworkedBlock<?>) BlockLoader.INSTANCE.getModdedFromVanillaBlock(Block.getById(nodeObject.get("blockID").asString()));
            AbstractNode abstractNode = block.createEmptyNode();
            abstractNode.load(this, nodeObject);
            this.nodeMap.put(abstractNode, abstractNode.getX(), abstractNode.getY(), abstractNode.getZ());
            this.nodeList.add(abstractNode);
            this.nodeCount.getAndIncrement();
        }

        this.netID.set(networkObject.get("netID").asInt());
    }

    public AbstractNode getNode(int x, int y, int z) {
        return nodeMap.get(x, y, z);
    }

    public void setGroup(NetworkGroup<?> group) {
        this.group = group;
    }

    public NetworkGroup<?> getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return String.format("{ NetworkID: %d, NodeCount: %d, NodeList: %s }", netID.get(), nodeCount.get(), nodeList);
    }
}
