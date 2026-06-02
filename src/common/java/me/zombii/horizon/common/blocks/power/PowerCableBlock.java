package me.zombii.horizon.common.blocks.power;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.BlockLoader;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.blocks.CableConnectorFunction;
import me.zombii.horizon.common.network.NetworkManager;
import me.zombii.horizon.common.network.power.IPowerBlock;
import me.zombii.horizon.common.network.power.IPowerHubBlockEntity;
import me.zombii.horizon.common.network.power.PowerNetwork;

public class PowerCableBlock implements IPowerBlock {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "power-cable");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;
    private final CableConnectorFunction connector;

    public PowerCableBlock() {
        this.blockGenerator = new BlockGenerator(ID);

        this.eventGenerator = new BlockEventGenerator(BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID, ID);
        this.eventGenerator.inheritParentContents();
        this.eventGenerator.inject(-1, "onPlace", this::onPlace);
        this.eventGenerator.inject(0, "onBreak", this::onBreak);
//        this.eventGenerator.inject(-1, "onInteract", this::onInteract);

        this.connector = new CableConnectorFunction(this, ID, Identifier.of(HorizonCommon.NAMESPACE, "models/blocks/wire-normal.json"));

        State defaultState = this.blockGenerator.createState("default");
        defaultState.lightAttenuation = 0;
        defaultState.isOpaque.set(false);
        defaultState.canWalkThrough.set(true);
        defaultState.modelId = this.connector.defaultModelName;
        defaultState.blockEventId = this.eventGenerator.getId();
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        PowerNetwork powerNetwork = NetworkManager.findNetwork(IPowerBlock.class, IPowerHubBlockEntity.NETWORK_DISCOVERY_FUNCTION, args.blockPos);
        if (powerNetwork == null) return;

        NetworkManager.build(powerNetwork, args.blockPos, false);
    }

    @Override
    public void onBreak(BlockEventArgs args) {
        PowerNetwork powerNetwork = NetworkManager.findNetwork(IPowerBlock.class, IPowerHubBlockEntity.NETWORK_DISCOVERY_FUNCTION, args.blockPos);
        if (powerNetwork == null) return;

        powerNetwork.removeNode(args.blockPos);
    }

    @Override
    public void onInteract(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    @Override
    public void onRegistered(Block block) {
        ISidedBlockConnector blockConnector = ISidedBlockConnector.getInstance();
        blockConnector.registerAsConnectedBlock(block, this.connector);

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
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction, BlockState target) {
        IGameTagList list = target.getTags();
        if (list == null) return false;
        IPowerBlock block = (IPowerBlock) BlockLoader.INSTANCE.getModdedFromVanillaBlock(target.getBlock());
        boolean passesTags = list.contains(HorizonTags.TAG_POWER_HUB) || list.contains(HorizonTags.TAG_POWER_CABLE) ||  list.contains(HorizonTags.TAG_POWER_SOURCE);
        if (block == null || block == this) return passesTags;
        return passesTags && block.canConnect(target, direction.getOpposite(), state);
    }

    @Override
    public Direction[] getConnectionFaces(BlockState state) {
        return Direction.ALL_DIRECTIONS;
    }
}
