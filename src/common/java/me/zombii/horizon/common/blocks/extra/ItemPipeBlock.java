package me.zombii.horizon.common.blocks.extra;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.connectedblocks.PipeConnectorFunction;

public class ItemPipeBlock implements IModBlock {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "item-pipe");

    private final BlockGenerator blockGenerator;
    private final PipeConnectorFunction connector;

    public ItemPipeBlock() {
        this.blockGenerator = new BlockGenerator(ID);
        this.connector = new PipeConnectorFunction(
                ID,
                Identifier.of(HorizonCommon.NAMESPACE, "models/pipes/solid"),
                "pipe",
                false
        );

        State defaultState = this.blockGenerator.createState("default");
        defaultState.modelId = this.connector.collisionModelNameA;
        defaultState.isOpaque.set(false);
        defaultState.lightAttenuation = 0;
        defaultState.itemIcon = Identifier.of(HorizonCommon.NAMESPACE, "textures/items/pipe-item.png").toString();
    }

    @Override
    public BlockGenerator getGenerator() {
        return blockGenerator;
    }

    @Override
    public void onRegistered(Block block) {
        ISidedBlockConnector.getInstance().registerAsConnectedBlock(block, this.connector);
        for (BlockState value : block.blockStates.values()) {
            value.tags.add(HorizonTags.TAG_ITEM_PIPE);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
