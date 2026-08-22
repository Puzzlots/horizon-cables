package me.zombii.horizon.common.wired.network;

import java.util.Objects;

public final class NodeReference {
    private final NetworkGroup<?> group;
    private final int networkID;
    private final int x;
    private final int y;
    private final int z;

    public NodeReference(
            NetworkGroup<?> group,
            int networkID,
            int x, int y, int z
    ) {
        this.group = group;
        this.networkID = networkID;
        this.x = x;
        this.y = y;
        this.z = z;
        if (group.get(networkID) == null)
            throw new IllegalStateException("Created reference with null network!");
    }

    public AbstractNetwork getNetwork() {
        return group.get(networkID);
    }

    public AbstractNode getNode() {
        return getNetwork().getNode(x, y, z);
    }

    public NetworkGroup<?> group() {
        return group;
    }

    public int networkID() {
        return networkID;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NodeReference) obj;
        return Objects.equals(this.group, that.group) &&
                this.networkID == that.networkID &&
                this.x == that.x &&
                this.y == that.y &&
                this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, networkID, x, y, z);
    }

    @Override
    public String toString() {
        return "NodeReference[" +
                "group=" + group + ", " +
                "networkID=" + networkID + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "z=" + z + ']';
    }


}
