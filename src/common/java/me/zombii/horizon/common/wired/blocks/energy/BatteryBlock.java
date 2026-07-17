package me.zombii.horizon.common.wired.blocks.energy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.BlockModelGenerator;
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
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.NetworkManager;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyBlock;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyHubBlockEntity;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyBatteryNode;

import java.util.Arrays;
import java.util.List;

public class BatteryBlock implements IEnergyBlock {

    private static final Direction[] PORTS = {
            Direction.POS_X, Direction.POS_Z,
            Direction.NEG_X, Direction.NEG_Z
    };

    private static final List<Direction> PORT_LIST = Arrays.asList(PORTS);

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "energy-battery-block");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;
    private final EnhancedBlockModelGenerator modelGenerator;

    public BatteryBlock() {
        FileHandle modelFile = GameAssetLoader.loadAsset(Identifier.of(HorizonCommon.NAMESPACE, "energy/large-battery.json"));

        this.blockGenerator = new BlockGenerator(ID);
        this.modelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsPlanes(
                "horizon-energy-battery-model",
                modelFile.readString(),
                false
        );

        // adding collision parts, no texture.
        ModelCuboid collisionCube = new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        Arrays.fill(collisionCube.faces, null);
        this.modelGenerator.getGroup("base").cuboids.add(collisionCube);

        this.eventGenerator = new BlockEventGenerator(BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID, ID);
        this.eventGenerator.inheritParentContents();
        this.eventGenerator.inject(-1, "onPlace", this::onPlace);
        this.eventGenerator.inject(0, "onBreak", this::onBreak);

        State defaultState = this.blockGenerator.createState("default");
        defaultState.isOpaque.set(false);
        defaultState.modelId = this.modelGenerator.getName();
        defaultState.lightAttenuation = 0;
        defaultState.blockEventId = this.eventGenerator.getId();
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
        
        EnergyNetwork network = NetworkManager.findNetwork(
                IEnergyBlock.class,
                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
                args.blockPos
        );

        if (network == null) return;

        NetworkManager.build(network, args.blockPos, false);
    }

    @Override
    public void onBreak(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        EnergyNetwork network = NetworkManager.findNetwork(
                IEnergyBlock.class,
                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
                args.blockPos
        );

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
        IGameTagList list = state.getTags();
        return list.contains(HorizonTags.TAG_ENERGY_COMPATIBLE);
    }

    @Override
    public AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        EnergyBatteryNode node = new EnergyBatteryNode(network, pos, state, this);
        node.setEnergyConsumedPerTick(1);
        node.setEnergyProducedPerTick(1);
        return node;
    }

    @Override
    public AbstractNode createEmptyNode() {
        return new EnergyBatteryNode();
    }

    @Override
    public BlockGenerator getGenerator() {
        return blockGenerator;
    }

    @Override
    public BlockModelGenerator[] getModelGenerators() {
        return new BlockModelGenerator[]{modelGenerator};
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
        for (BlockState value : block.blockStates.values()) {
            value.tags.add(HorizonTags.TAG_ENERGY_COMPATIBLE);
        }
    }
}
