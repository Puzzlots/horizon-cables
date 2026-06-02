package me.zombii.horizon.common.blocks;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.util.Identifier;

import static me.zombii.horizon.common.HorizonCommon.BLOCK_MAP;

public class FlatWireBlock implements IModBlock {

    static final Identifier ID = Identifier.of("horizon", "flat_wire");
    final BlockGenerator generator;
    final BlockEventGenerator eventGenerator;
    final FlatWireBlockConnectorFunction connectorFunction;

    public FlatWireBlock() {
        generator = new BlockGenerator(ID);
        connectorFunction = new FlatWireBlockConnectorFunction(ID);
        eventGenerator = new BlockEventGenerator(BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID, ID);
        eventGenerator.inheritParentContents();
        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(-1, "onBreak", this::onBreak);
        State defaultState = generator.createState("default");
        defaultState.modelId = connectorFunction.defaultModelName;
        defaultState.blockEventId = eventGenerator.getId();
        defaultState.isOpaque.set(false);
        defaultState.canWalkThrough.set(true);
        defaultState.lightAttenuation = 0;

    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!ID.toString().equals(args.srcBlockState.getBlockId())) {
            return;
        }

    }

    @Override
    public void onBreak(BlockEventArgs args) {
        if (!ID.toString().equals(args.srcBlockState.getBlockId())) {
            return;
        }
    }


    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{eventGenerator};
    }

    @Override
    public BlockGenerator getGenerator() {
        return generator;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onRegistered(Block block) {
        BLOCK_MAP.put(block, this);

        ISidedBlockConnector connector = ISidedBlockConnector.getInstance();
        connector.registerAsConnectedBlock(block, connectorFunction);
        for (BlockState blockState : block.blockStates.values()) {
            blockState.initTagList();
        }
    }

}
