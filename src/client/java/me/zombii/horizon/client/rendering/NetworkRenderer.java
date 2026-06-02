package me.zombii.horizon.client.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import me.zombii.horizon.common.network.*;
import me.zombii.horizon.common.network.power.BatteryNode;
import me.zombii.horizon.common.network.power.PowerCableNode;
import me.zombii.horizon.common.network.power.PowerHubNode;
import me.zombii.horizon.common.network.power.PowerNetwork;

import java.util.ArrayList;
import java.util.List;

public class NetworkRenderer {

    private static final Vector3 tmpVecA = new Vector3();
    private static final Vector3 tmpVecB = new Vector3();

    public static void renderAllNetworks(ShapeRenderer renderer) {
//        List<AbstractNetwork> networks = NetworkManager.networks;
        List<PowerNetwork> networks = NetworkGroups.powerNetworkGroup.getNetworks();
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
            renderer.setColor(Color.WHITE);

            if (node instanceof BatteryNode) {
                nodeScale = .5f;
                renderer.setColor(Color.GREEN);
            }
            if (node instanceof PowerCableNode) {
//                nodeScale = .25f;
                renderer.setColor(Color.WHITE);
            }
            if (node instanceof PowerHubNode) {
                nodeScale = 1f;
                renderer.setColor(Color.BLUE);
            }

            tmpVecA.set(node.getX(), node.getY(), node.getZ());
            tmpVecA.add(.5f, .5f, .5f);
            tmpVecB.set(tmpVecA);
            tmpVecB.sub((nodeScale / 2), (nodeScale / 2), -(nodeScale / 2));

            tmpVecA.add(0, 2, 0);
            tmpVecB.add(0, 2, 0);

            renderer.box(tmpVecB.x, tmpVecB.y, tmpVecB.z, nodeScale, nodeScale, nodeScale);

            NodeReference[] connections = node.getConnections();
            renderer.setColor(Color.RED);
            for (NodeReference ref : connections) {
                if (ref == null) continue;

                tmpVecB.set(ref.x(), ref.y(), ref.z());
                tmpVecB.add(.5f, .5f, .5f);
                tmpVecB.add(0, 2, 0);
                renderer.line(tmpVecA, tmpVecB);
            }
        }

        renderer.end();
    }

}
