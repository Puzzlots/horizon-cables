package me.zombii.horizon.common.wired.network.energy.nodes;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.INetworkedBlock;
import me.zombii.horizon.common.wired.network.NodeReference;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class EnergyNodeCable extends EnergyNode {

    public EnergyNodeCable() {
        super();
    }

    public EnergyNodeCable(AbstractNetwork network, IReadBlockPosition pos, BlockState state, INetworkedBlock<?> block) {
        super(network, pos, state, block);
    }

    @Override
    public void onPowerOn(Direction direction) {
        Set<EnergyNode> visited = new HashSet<>();
        Set<NodeReference> visited2 = new HashSet<>();
        ObjectList<EnergyNodeCable> nodes = new ObjectArrayList<>();
        nodes.add(this);
        spreadPower(visited, direction, true);
//        spreadPower2(nodes, visited2, direction, true);
    }

    @Override
    public void onPowerOff(Direction direction) {
        Set<EnergyNode> visited = new HashSet<>();
        Set<NodeReference> visited2 = new HashSet<>();
        ObjectList<EnergyNodeCable> nodes = new ObjectArrayList<>();
        nodes.add(this);
        spreadPower(visited, direction, false);
//        spreadPower2(nodes, visited2, direction, false);
    }

    private void spreadPower2(
            ObjectList<EnergyNodeCable> queue,
            Set<NodeReference> visited,
            Direction direction,
            boolean powerOn
    ) {
        push(() -> {
            if (!queue.isEmpty()) {
                EnergyNodeCable cable = queue.removeFirst();
                if (!visited.add(cable.getRef())) return;

                for (int i = 0; i < cable.getConnections().length; i++) {
                    NodeReference connection = cable.getConnections()[i];
                    if (connection != null && connection.getNetwork() != null) {
                        EnergyNode con = (EnergyNode) connection.getNode();
                        if (con instanceof EnergyNodeCable c) {
                            queue.add(c);
                            continue;
                        }
                        int finalI = i;
                        if (powerOn) {
                            push(() -> con.powerOn(Direction.ALL_DIRECTIONS[finalI]));
                            continue;
                        }
                        push(() -> con.powerOn(Direction.ALL_DIRECTIONS[finalI]));
                    }
                }
            }
        });
    }

    private void spreadPower(Set<EnergyNode> visited, Direction powerDir, boolean power) {
        ObjectList<NodeReference> references = new ObjectArrayList<>();
        ObjectList<Direction> directions = new ObjectArrayList<>();

        references.add(this.getRef());
        directions.add(powerDir);
        while (!references.isEmpty() && !directions.isEmpty()) {
            NodeReference lastConn = references.removeLast();
            Direction direction = directions.removeLast();
            if (lastConn.getNetwork() == null) continue;
            AbstractNode node = lastConn.getNode();
            if (node instanceof EnergyNode eNode) {
                if (!visited.add(eNode)) continue;
                if (node instanceof EnergyNodeCable cable) {
                    eNode.setPowered(power);
                    for (int i = 0; i < cable.getConnections().length; i++) {
                        NodeReference connection = cable.getConnections()[i];
                        if (connection != null && connection.getNetwork() != null) {
                            references.add(connection);
                            directions.add(Direction.ALL_DIRECTIONS[i]);
                        }
                    }
                } else {
                    if (power) eNode.powerOn(direction);
                    else eNode.powerOff(direction);
//                    System.out.println(eNode);
//                    if (isPowered()) push(() -> eNode.powerOn(direction));
//                    else push((() -> eNode.powerOff(direction)));
                }
            }
        }
    }

}
