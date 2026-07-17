package me.zombii.horizon.common.wired.network;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.BlockLoader;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.util.constants.Direction;

import java.util.*;
import java.util.function.BiFunction;

public class NetworkManager {

    public static <T extends AbstractNetwork, N extends INetworkedBlock<T>> T findNetwork(
            Class<N> blockType,
            BiFunction<N, IReadBlockPosition, T> searchFunction,
            IReadBlockPosition start
    ) {
        Queue<IReadBlockPosition> queue = new LinkedList<>();
        Set<IReadBlockPosition> visited = new HashSet<>();

        queue.add(start);

        while (!queue.isEmpty()) {
            IReadBlockPosition currentPos = queue.poll();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);

            BlockState currentState = currentPos.getBlockState();
            if (currentState == null) continue;

            IModBlock currentModBlock = BlockLoader.INSTANCE.getModdedFromVanillaBlock(currentState.getBlock());
            if (!blockType.isInstance(currentModBlock)) continue;
            @SuppressWarnings("unchecked")
            N networkedBlock = (N) currentModBlock;

            T network = searchFunction.apply(networkedBlock, currentPos);
            if (network != null) return network;

            for (Direction direction : networkedBlock.getConnectionFaces(currentState)) {
                BlockPosition neighborPos = currentPos.getOffsetBlockPos(currentPos.getZone(), direction);

                BlockState neighborState = neighborPos.getBlockState();
                if (neighborState == null) continue;

                IModBlock neighborModBlock = BlockLoader.INSTANCE.getModdedFromVanillaBlock(neighborState.getBlock());
                if (!blockType.isInstance(neighborModBlock)) continue;

                @SuppressWarnings("unchecked")
                N neighborNetworkedBlock = (N) neighborModBlock;

                boolean canConnectA = networkedBlock.canConnect(neighborState, direction, neighborPos);
                boolean canConnectB = neighborNetworkedBlock.canConnect(neighborState, direction.getOpposite(), currentPos);

                if (canConnectA && canConnectB) queue.add(neighborPos);
            }
        }
        return null;
    }

    public static <T extends AbstractNetwork> T build(T network, IReadBlockPosition start, boolean clear) {
        if (!(BlockLoader.INSTANCE.getModdedFromVanillaBlock(start.getBlockState().getBlock()) instanceof INetworkedBlock)) return network;
        if (clear) network.clear();

        Class<?> networkClass = network.getNetworkClass();

        Queue<IReadBlockPosition> queue = new LinkedList<>();
        Set<IReadBlockPosition> visited = new HashSet<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            IReadBlockPosition current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            BlockState currentState = current.getBlockState();
            Object block = BlockLoader.INSTANCE.getModdedFromVanillaBlock(currentState.getBlock());
            if (networkClass.isInstance(block)) {
                INetworkedBlock<?> networkedBlock = (INetworkedBlock<?>) block;

                AbstractNode currentNode = network.getOrCreateNode(current, currentState, networkedBlock);
                NodeReference[] connections = currentNode.getConnections();

                for (Direction direction : networkedBlock.getConnectionFaces(currentState)) {
                    BlockPosition neighbor = current.getOffsetBlockPos(current.getZone(), direction);
                    BlockState neighborState = neighbor.getBlockState();
                    Object block2 = BlockLoader.INSTANCE.getModdedFromVanillaBlock(neighborState.getBlock());
                    if (networkClass.isInstance(block2)) {
                        INetworkedBlock<?> networkedBlock2 = (INetworkedBlock<?>) block2;

                        AbstractNode neighborNode = network.getOrCreateNode(neighbor, neighborState, networkedBlock2);
                        NodeReference[] neighborConnections = neighborNode.getConnections();

                        boolean canConnectA = networkedBlock.canConnect(
                                currentState, direction,
                                neighbor
                        );
                        boolean canConnectB = networkedBlock2.canConnect(
                                currentState, direction.getOpposite(),
                                current
                        );
                        if (canConnectA && canConnectB) {
                            connections[direction.ordinal()] = neighborNode.getRef();
                            neighborConnections[direction.getOpposite().ordinal()] = currentNode.getRef();

                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return network;
    }

}
