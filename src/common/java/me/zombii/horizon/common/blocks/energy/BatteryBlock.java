package me.zombii.horizon.common.blocks.energy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.BlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.enhanced.EnhancedBlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.network.AbstractNetwork;
import me.zombii.horizon.common.network.AbstractNode;
import me.zombii.horizon.common.network.energy.interfaces.IEnergyBlock;
import me.zombii.horizon.common.network.energy.nodes.EnergyBatteryNode;

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
    private final EnhancedBlockModelGenerator modelGenerator;

    public BatteryBlock() {
        FileHandle modelFile = GameAssetLoader.loadAsset(Identifier.of(HorizonCommon.NAMESPACE, "blocks/models/large-battery.json"));

        this.blockGenerator = new BlockGenerator(ID);
        this.modelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsCuboids(
                "horizon-energy-battery-model",
                modelFile.readString()
        );

        // adding collision parts, no texture.
        this.modelGenerator.getGroup("base").cuboids.add(new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16)));

        State defaultState = this.blockGenerator.createState("default");
        defaultState.isOpaque.set(false);
        defaultState.modelId = this.modelGenerator.getName();
        defaultState.lightAttenuation = 0;
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
    public Identifier getId() {
        return ID;
    }
}
