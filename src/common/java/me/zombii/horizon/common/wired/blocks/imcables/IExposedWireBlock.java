package me.zombii.horizon.common.wired.blocks.imcables;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.enhanced.EnhancedBlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.wired.connectedblocks.CableConnectorFunctionBe;
import me.zombii.horizon.immersivecables.be.WireBE;
import org.hjson.JsonValue;

import java.util.Arrays;
import java.util.List;

public class IExposedWireBlock implements IModBlock {

    public static final Identifier GENERAL_ID = Identifier.of(HorizonCommon.NAMESPACE, "i-exposed-wire-block");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;
    private final EnhancedBlockModelGenerator modelGenerator;
    private final CableConnectorFunctionBe connector;
    private final Identifier id;

    public IExposedWireBlock() {
        FileHandle modelFile = GameAssetLoader.loadAsset(
                Identifier.of(HorizonCommon.NAMESPACE, "imcables/cables/exposed/wire-exposed.json")
        );

        id = Identifier.of(
                GENERAL_ID.getNamespace(),
                GENERAL_ID.getName()
        );

        this.blockGenerator = new BlockGenerator(id);
        this.blockGenerator.setBlockEntity(Identifier.of(WireBE.ID));
        this.blockGenerator.getBlockEntityParams().put("channel", JsonValue.valueOf(WireBE.UNIVERSAL_CHANNEL));

        this.modelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsPlanes(
                "immersive-exposed-cables-model",
                modelFile.readString(),
                false
        );

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "iwire-exposed-block-events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);

        // adding collision parts, no texture.
        ModelCuboid collisionCube = new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        Arrays.fill(collisionCube.faces, null);
//        this.modelGenerator.getGroup("base").cuboids.add(collisionCube);
        this.modelGenerator.getGroup("CENTER").cuboids.add(collisionCube);

        this.connector = new CableConnectorFunctionBe(this, modelGenerator, id);

        State defaultState = this.blockGenerator.createState("default");
        defaultState.blockEventId = eventGenerator.getId();
        defaultState.modelId = connector.defaultModelName;
        defaultState.isOpaque.set(false);
        defaultState.lightAttenuation = 0;
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    public void onBreak(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{eventGenerator};
    }

    @Override
    public BlockGenerator getGenerator() {
        return blockGenerator;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void onRegistered(Block block) {
        for (BlockState value : block.blockStates.values()) {
            value.initTagList();
            value.tags.add(HorizonTags.TAG_ENERGY_COMPATIBLE);
            value.tags.add(HorizonTags.TAG_CABLE_CONNECTABLE);
        }

        ISidedBlockConnector.getInstance()
                .registerAsConnectedBlock(block, connector);
    }
}
