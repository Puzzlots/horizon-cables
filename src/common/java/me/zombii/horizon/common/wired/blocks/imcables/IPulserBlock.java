package me.zombii.horizon.common.wired.blocks.imcables;

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
import me.zombii.horizon.immersivecables.be.LaserPulserBE;
import org.hjson.JsonArray;
import org.hjson.JsonValue;

import java.util.Arrays;

public class IPulserBlock implements IModBlock {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "i-pulser-block");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;

    public IPulserBlock() {
        this.blockGenerator = new BlockGenerator(ID);
        this.blockGenerator.setBlockEntity(Identifier.of(LaserPulserBE.ID));
        JsonArray portArrayA = new JsonArray().add(Direction.NEG_Z.toString());
        JsonArray portArrayB = new JsonArray().add(Direction.POS_Z.toString());
        this.blockGenerator.getBlockEntityParams().put("inPorts", portArrayA);
        this.blockGenerator.getBlockEntityParams().put("outPorts", portArrayB);
        this.blockGenerator.getBlockEntityParams().put("rotatePorts", JsonValue.valueOf(true));

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "ipulser-block-events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);

        // adding collision parts, no texture.
        ModelCuboid collisionCube = new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        Arrays.fill(collisionCube.faces, null);
//        this.modelGenerator.getGroup("base").cuboids.add(collisionCube);
        State defaultProperties = this.blockGenerator.getDefaultProperties();
        defaultProperties.blockEventId = eventGenerator.getId();
        defaultProperties.modelId = "horizon:imcables/pulse-converter.json";
        defaultProperties.isOpaque.set(false);
        defaultProperties.isCatalogHidden.set(true);
        defaultProperties.lightAttenuation = 0;
        defaultProperties.dropId = ID.toString() + "[direction=NegX]";
        defaultProperties.placementRules = "omnidirectional_towards";

        State negX = this.blockGenerator.createState("direction=NegX");
        negX.rotation[1] = 90;
        negX.isCatalogHidden.set(false);

        State posX = this.blockGenerator.createState("direction=PosX");
        posX.rotation[1] = 270;

        State negZ = this.blockGenerator.createState("direction=NegZ");
        negZ.rotation[1] = 180;

        State posZ = this.blockGenerator.createState("direction=PosZ");
        posZ.rotation[1] = 0;

        State negY = this.blockGenerator.createState("direction=NegY");
        negY.rotation[0] = 270;

        State posY = this.blockGenerator.createState("direction=PosY");
        posY.rotation[0] = 90;
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
        DirectionUtil.flipOnSneak(args);
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
