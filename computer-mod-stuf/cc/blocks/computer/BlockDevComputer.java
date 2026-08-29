package me.zombii.horizon.common.cc.blocks.computer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.BlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.enhanced.EnhancedBlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import me.zombii.horizon.common.HorizonCommon;

import java.util.Arrays;

public class BlockDevComputer implements IModBlock {

    public static final Identifier SCREEN_ID = Identifier.of(HorizonCommon.NAMESPACE, "dev-computer-screen");
    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "dev-computer-block");
    public static final Identifier BE_ID = Identifier.of(HorizonCommon.NAMESPACE, "dev-computer-be");
    private final EnhancedBlockModelGenerator modelGenerator;

    public BlockDevComputer() {
        FileHandle modelFile = GameAssetLoader.loadAsset(
                Identifier.of(HorizonCommon.NAMESPACE, "cc/computer-model.json")
        );

        this.modelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsPlanes(
                "cc-dev-computer-model",
                modelFile.readString(),
                false
        );

        // adding collision parts, no texture.
        ModelCuboid collisionCube = new ModelCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        Arrays.fill(collisionCube.faces, null);
//        this.modelGenerator.getGroup("base").cuboids.add(collisionCube);
        this.modelGenerator.getGroup("bone").cuboids.add(collisionCube);
    }

    @Override
    public BlockGenerator getGenerator() {
        BlockGenerator generator = new BlockGenerator(ID);
        generator.setBlockEntity(BE_ID);
        State defaultProperties = generator.getDefaultProperties();
        defaultProperties.modelId = "cc-dev-computer-model";
        defaultProperties.isOpaque.set(false);
        defaultProperties.isCatalogHidden.set(true);
        defaultProperties.lightAttenuation = 0;
        defaultProperties.dropId = ID.toString() + "[direction=NegX]";

        State negX = generator.createState("direction=NegX");
        negX.rotation[1] = 90;
        negX.isCatalogHidden.set(false);

        State posX = generator.createState("direction=PosX");
        posX.rotation[1] = 270;

        State negZ = generator.createState("direction=NegZ");
        negZ.rotation[1] = 180;

        State posZ = generator.createState("direction=PosZ");
        posZ.rotation[1] = 0;

        State negY = generator.createState("direction=NegY");
        negY.rotation[0] = 270;

        State posY = generator.createState("direction=PosY");
        posY.rotation[0] = 90;
        
        State state = generator.createState("default");
        return generator;
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
