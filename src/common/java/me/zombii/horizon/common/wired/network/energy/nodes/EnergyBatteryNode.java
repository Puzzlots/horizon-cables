package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyConsumerNode;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyProducerNode;
import org.hjson.JsonObject;

public class EnergyBatteryNode extends AbstractNode implements IEnergyProducerNode, IEnergyConsumerNode {

    private double unitsConsumedPerTick = 0;
    private double unitsProducedPerTick = 0;

    public EnergyBatteryNode() {
        super();
    }

    public EnergyBatteryNode(
            AbstractNetwork network,
            IReadBlockPosition pos,
            BlockState state,
            INetworkedBlock<?> block
    ) {
        super(network, pos, state, block);
    }

    @Override
    public double getEnergyConsumedPerTick() {
        return unitsConsumedPerTick;
    }

    @Override
    public void setEnergyConsumedPerTick(double energyConsumedPerTick) {
        this.unitsConsumedPerTick = energyConsumedPerTick;
    }

    @Override
    public double getEnergyProducedPerTick() {
        return unitsProducedPerTick;
    }

    @Override
    public void setEnergyProducedPerTick(double energyProducedPerTick) {
        this.unitsProducedPerTick = energyProducedPerTick;
    }

    @Override
    public void save(JsonObject nodeObject) {
        super.save(nodeObject);
        nodeObject.set("producedPerTick", unitsProducedPerTick);
        nodeObject.set("consumedPerTick", unitsConsumedPerTick);
    }

    @Override
    public void load(AbstractNetwork network, JsonObject nodeObject) {
        super.load(network, nodeObject);
        setEnergyConsumedPerTick(nodeObject.getDouble("producedPerTick", 0));
        setEnergyConsumedPerTick(nodeObject.getDouble("consumedPerTick", 0));
    }
}
