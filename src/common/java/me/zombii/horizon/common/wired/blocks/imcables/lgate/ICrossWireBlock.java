package me.zombii.horizon.common.wired.blocks.imcables.lgate;

import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.DirectionUtil;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.LogicGate;
import me.zombii.horizon.immersivecables.be.CrossWireBE;
import me.zombii.horizon.immersivecables.be.LogicGateBE;
import org.hjson.JsonArray;
import org.hjson.JsonValue;

import java.util.Arrays;

public class ICrossWireBlock implements IModBlock {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "i-cross-wire-block");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;

    public ICrossWireBlock() {
        this.blockGenerator = new BlockGenerator(ID);
        this.blockGenerator.setBlockEntity(Identifier.of(CrossWireBE.ID));
        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "icross-wire-block-events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);

        // adding collision parts, no texture.
        ModelCuboid collisionCube = new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        Arrays.fill(collisionCube.faces, null);
//        this.modelGenerator.getGroup("base").cuboids.add(collisionCube);
        State defaultState = this.blockGenerator.createState("default");
        defaultState.blockEventId = eventGenerator.getId();
        defaultState.modelId = "horizon:imcables/cables/cross-wire.json";
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
        return ID;
    }

    @Override
    public void onRegistered(Block block) {
        for (BlockState value : block.blockStates.values()) {
            value.initTagList();
            value.tags.add(HorizonTags.TAG_ENERGY_COMPATIBLE);
            value.tags.add(HorizonTags.TAG_CABLE_CONNECTABLE);
        }
    }
}
