package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;
import org.hjson.JsonObject;

public abstract class EnergyNode extends AbstractNode {

    public static final ObjectList<Runnable> buffer = new ObjectArrayList<>();

    public static void push(Runnable r) {
        buffer.addLast(r);
    }

    public EnergyNode() {
        super();
    }

    public EnergyNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    private boolean powered = false;

    public void setPowered(boolean powered) {
        this.powered = powered;
        System.out.println("Turned " + (powered ? "on" : "off") + " " + this);
    }

    public void powerOff(Direction direction) {
        if (!GameSingletons.isHost()) return;
        if (!isPowered()) return;
        onPowerOff(direction);
        setPowered(false);
    }

    public void powerOn(Direction direction) {
        if (!GameSingletons.isHost()) return;
        if (isPowered()) return;
        onPowerOn(direction);
        setPowered(true);
    }

    protected void onPowerOn(Direction direction) {

    }

    protected void onPowerOff(Direction direction) {

    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public void save(JsonObject nodeObject) {
        super.save(nodeObject);
        nodeObject.set("isPowered", powered);
    }

    @Override
    public void load(AbstractNetwork network, JsonObject nodeObject) {
        super.load(network, nodeObject);
        powered = nodeObject.getBoolean("isPowered", false);
    }
}
