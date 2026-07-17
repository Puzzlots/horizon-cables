package me.zombii.horizon.common.wired.network;

public record NodeReference(
        NetworkGroup<?> group,
        int networkID,
        int x, int y, int z
) {

    public AbstractNetwork getNetwork() {
        return group.get(networkID);
    }

    public AbstractNode getNode() {
        return getNetwork().getNode(x, y, z);
    }

}
