package me.zombii.horizon.common.network.power;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.INetworkedBlock;
import me.zombii.horizon.common.network.NodeReference;

public class BatteryNode extends AbstractNode {

    public BatteryNode() {
        super();
    }

    public BatteryNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    public NodeReference getPositiveConnection() {
        return getConnections()[POS_Z];
    }

    public NodeReference getNegativeConnection() {
        return getConnections()[NEG_Z];
    }

    private double voltage;

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
}
