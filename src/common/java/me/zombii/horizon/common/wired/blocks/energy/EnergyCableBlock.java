package me.zombii.horizon.common.wired.blocks.energy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
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
import me.zombii.horizon.common.wired.connectedblocks.CableConnectorFunction2;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.NetworkManager;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyBlock;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyHubBlockEntity;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeCable;

import java.util.Arrays;
import java.util.List;

// Very unfinished
public class EnergyCableBlock implements IEnergyBlock {

    private static final Direction[] PORTS = {
            Direction.POS_X, Direction.POS_Z,
            Direction.NEG_X, Direction.NEG_Z,
            Direction.POS_Y, Direction.NEG_Y
    };

    private static final List<Direction> PORT_LIST = Arrays.asList(PORTS);

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "energy-cable");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;
    private final EnhancedBlockModelGenerator modelGenerator;
    private final CableConnectorFunction2 connector;

    public EnergyCableBlock() {
        FileHandle modelFile = GameAssetLoader.loadAsset(Identifier.of(HorizonCommon.NAMESPACE, "imcables/cables/insulated/wire-insulated.json"));

        this.blockGenerator = new BlockGenerator(ID);

        this.modelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsPlanes(
                "horizon-energy-cable-model",
                modelFile.readString(),
                false
        );

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "energy_cable_block_events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);

        // adding collision parts, no texture.
        ModelCuboid collisionCube = new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        Arrays.fill(collisionCube.faces, null);
//        this.modelGenerator.getGroup("base").cuboids.add(collisionCube);
        this.modelGenerator.getGroup("CENTER").cuboids.add(collisionCube);

        this.connector = new CableConnectorFunction2(this, modelGenerator, ID);

        State defaultState = this.blockGenerator.createState("default");
        defaultState.blockEventId = eventGenerator.getId();
        defaultState.modelId = connector.defaultModelName;
        defaultState.isOpaque.set(false);
        defaultState.lightAttenuation = 0;
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        EnergyNetwork network = NetworkManager.findNetwork(
                IEnergyBlock.class,
                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
                args.blockPos
        );

        System.out.println(network);
        if (network == null) return;

        NetworkManager.build(network, args.blockPos, false);
    }

    public void onBreak(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        EnergyNetwork network = NetworkManager.findNetwork(
                IEnergyBlock.class,
                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
                args.blockPos
        );

        System.out.println(network);
        if (network == null) return;

        network.removeNode(args.blockPos);
    }

    @Override
    public Direction[] getConnectionFaces(BlockState state) {
        return PORTS;
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction, BlockState target) {
        if (!PORT_LIST.contains(direction)) return false;

        IGameTagList list = target.getTags();
        return list.contains(HorizonTags.TAG_ENERGY_COMPATIBLE);
    }

    @Override
    public AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        return new EnergyNodeCable(network, pos, state, this);
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{eventGenerator};
    }

    @Override
    public AbstractNode createEmptyNode() {
        return new EnergyNodeCable();
    }

    @Override
    public BlockGenerator getGenerator() {
        return blockGenerator;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onRegistered(Block block) {
        for (BlockState value : block.blockStates.values()) {
            value.initTagList();
            value.tags.add(HorizonTags.TAG_ENERGY_COMPATIBLE);
            value.tags.add(HorizonTags.TAG_STOP_PISTON_PUSH);
            value.tags.add(HorizonTags.TAG_STOP_PISTON_PULL);
        }

        ISidedBlockConnector.getInstance()
                .registerAsConnectedBlock(block, connector);
    }
}
