package me.zombii.horizon.common.blocks;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonTags;

import static me.zombii.horizon.common.HorizonCommon.BLOCK_MAP;

public class DataCableBlock implements IModBlock {

    public static final Identifier ID =
            Identifier.of("horizon", "data-cable");

    final BlockGenerator generator;
    final BlockEventGenerator eventGenerator;
    final CableConnectorFunction connectorFunction;
    final ISidedBlockConnector connector;

    public DataCableBlock() {
        generator = new BlockGenerator(ID);

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "data_cable_block_events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);

        this.connectorFunction = null;
//        connectorFunction = new CableConnectorFunction(
//                this, ID,
//                Identifier.of("horizon", "models/blocks/data-cable.json")
//        );

        State defaultState = generator.createState("default");
        defaultState.modelId = connectorFunction.defaultModelName;
        defaultState.blockEventId = eventGenerator.getId();
        defaultState.isOpaque.set(false);
        defaultState.lightAttenuation = 0;

        connector = ISidedBlockConnector.getInstance();
    }

    @Override
    public void onPlace(BlockEventArgs args) {
//        ElectricalNetwork network = NetworkManager.findNetwork(IElectricBlock.class, WireHubBlock.NETWORK_DISCOVERY_FUNCTION, args.blockPos);
//        if (network == null) return;
//
//        NetworkManager.build(network, (BlockPosition) args.blockPos, true);
    }

    public void onBreak(BlockEventArgs args) {
//        ElectricalNetwork network = NetworkManager.findNetwork(IElectricBlock.class, WireHubBlock.NETWORK_DISCOVERY_FUNCTION, args.blockPos);
//        if (network == null) return;
//
//        network.removeNode(args.blockPos);
    }

    @Override
    public BlockGenerator getGenerator() {
        return generator;
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{eventGenerator};
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onRegistered(Block block) {
        connector.registerAsConnectedBlock(
                block,
                connectorFunction
        );
        BLOCK_MAP.put(block, this);

        for (BlockState value : block.blockStates.values()) {
            value.initTagList();

            value.tags.add(HorizonTags.TAG_POWER_CABLE);
        }
    }

//    @Override
//    public boolean canConnect(BlockState state, Direction direction, IReadBlockPosition target) {
//        IGameTagList list = target.getBlockState().getTags();
//        if (list == null) return false;
//        return list.contains(HorizonTags.TAG_POWER) || list.contains(HorizonTags.TAG_POWER_SOURCE) || list.contains(HorizonTags.TAG_POWER_HUB);
//    }
//
//    @Override
//    public Direction[] getConnectionFaces() {
//        return Direction.ALL_DIRECTIONS;
//    }
}