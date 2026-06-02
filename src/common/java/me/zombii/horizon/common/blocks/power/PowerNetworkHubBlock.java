package me.zombii.horizon.common.blocks.power;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.BlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.enhanced.EnhancedBlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.be.power.PowerNetworkHubBlockEntity;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.dcpower.IDCPowerBlock;
import me.zombii.horizon.common.network.dcpower.DCPowerHubNode;

public class PowerNetworkHubBlock implements IDCPowerBlock {
    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "power-network-hub");

    private final BlockGenerator blockGenerator;
    private final EnhancedBlockModelGenerator modelGenerator;
    private final BlockEventGenerator eventGenerator;

    public PowerNetworkHubBlock() {
        this.blockGenerator = new BlockGenerator(ID);
        this.blockGenerator.setBlockEntity(PowerNetworkHubBlockEntity.ID);

        this.eventGenerator = new BlockEventGenerator(BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID, ID);
        this.eventGenerator.inheritParentContents();
        this.eventGenerator.inject(-1, "onPlace", this::onPlace);
        this.eventGenerator.inject(0, "onBreak", this::onBreak);
//        this.eventGenerator.inject(-1, "onInteract", this::onInteract);

        this.modelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsCuboids(
                "power-network-hub-block-model",
                GameAssetLoader.loadAsset(Identifier.of(HorizonCommon.NAMESPACE, "models/blocks/power-network-hub.json")).readString()
        );

        State defaultState = this.blockGenerator.createState("default");
        defaultState.lightAttenuation = 0;
        defaultState.isOpaque.set(false);
        defaultState.canWalkThrough.set(true);
        defaultState.modelId = this.modelGenerator.getName();
        defaultState.blockEventId = this.eventGenerator.getId();
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    @Override
    public void onBreak(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    @Override
    public void onInteract(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    @Override
    public void onRegistered(Block block) {
        for (BlockState value : block.blockStates.values()) {
            value.tags.add(HorizonTags.TAG_POWER_HUB);
        }
    }

    @Override
    public BlockGenerator getGenerator() {
        return this.blockGenerator;
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{this.eventGenerator};
    }

    @Override
    public BlockModelGenerator[] getModelGenerators() {
        return new BlockModelGenerator[]{this.modelGenerator};
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public AbstractNode createEmptyNode() {
        return new DCPowerHubNode();
    }

    @Override
    public AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        return new DCPowerHubNode(network, pos, state, this);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction, BlockState target) {
        IGameTagList list = target.getTags();
        if (list == null) return false;
        if (direction != connectionFaces[0]) return false;
        return list.contains(HorizonTags.TAG_POWER_HUB) || list.contains(HorizonTags.TAG_POWER_CABLE) ||  list.contains(HorizonTags.TAG_POWER_SOURCE);
    }

    private final Direction[] connectionFaces = new Direction[]{Direction.POS_Y};

    @Override
    public Direction[] getConnectionFaces(BlockState state) {
        return connectionFaces;
    }

}
