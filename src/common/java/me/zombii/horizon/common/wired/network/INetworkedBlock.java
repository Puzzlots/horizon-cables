package me.zombii.horizon.common.wired.network;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.util.constants.Direction;

public interface INetworkedBlock<T extends AbstractNetwork> extends IModBlock {

    Direction[] getConnectionFaces(BlockState state);

    default boolean canConnect(
            BlockState state,
            Direction direction,
            IReadBlockPosition target
    ) {
        return canConnect(state, direction, target.getBlockState());
    }

    boolean canConnect(
            BlockState state,
            Direction direction,
            BlockState target
    );

    AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state);
    AbstractNode createEmptyNode();

}
