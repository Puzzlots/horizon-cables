package me.zombii.horizon.client.wired.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import me.zombii.horizon.common.wired.network.*;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNode;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeCable;

import java.util.List;

public class NetworkRenderer {

    private static final Vector3 tmpVecA = new Vector3();
    private static final Vector3 tmpVecB = new Vector3();

    public static void renderAllGroups(ShapeRenderer renderer) {
        NetworkGroups.GROUP_REGISTRY.forEach(group -> {
            renderGroup(renderer, group);
        });
    }

    public static void renderGroup(ShapeRenderer renderer, NetworkGroup<?> group) {
        List<AbstractNetwork> networks = (List<AbstractNetwork>) group.getNetworks();
        for (AbstractNetwork network : networks) {
            render(renderer, network);
        }
    }

    public static void render(ShapeRenderer renderer, AbstractNetwork network) {
        renderer.begin(ShapeRenderer.ShapeType.Line);

        List<AbstractNode> nodes = network.getNodeList();

        float nodeScale;
        for (AbstractNode node : nodes) {
            nodeScale = .25f;
            if (node instanceof EnergyNode eNode) {
                nodeScale = .3f;
                if (!(node instanceof EnergyNodeCable)) {
                    nodeScale = .5f;
                }
                renderer.setColor(eNode.isPowered() ? Color.GREEN : Color.RED);
            } else {
                renderer.setColor(Color.WHITE);
            }

            tmpVecA.set(node.getX(), node.getY(), node.getZ());
            tmpVecA.add(.5f, .5f, .5f);
            tmpVecB.set(tmpVecA);
            tmpVecB.sub((nodeScale / 2), (nodeScale / 2), -(nodeScale / 2));

//            tmpVecA.add(0, 2, 0);
//            tmpVecB.add(0, 2, 0);

            renderer.box(tmpVecB.x, tmpVecB.y, tmpVecB.z, nodeScale, nodeScale, nodeScale);

            NodeReference[] connections = node.getConnections();
            renderer.setColor(Color.YELLOW);
            for (NodeReference ref : connections) {
                if (ref == null) continue;

                tmpVecB.set(ref.x(), ref.y(), ref.z());
                tmpVecB.add(.5f, .5f, .5f);
//                tmpVecB.add(0, 2, 0);
                renderer.line(tmpVecA, tmpVecB);
            }
        }

        renderer.end();
    }

}
