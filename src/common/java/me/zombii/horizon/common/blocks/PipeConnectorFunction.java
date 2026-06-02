package me.zombii.horizon.common.blocks;

import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.BlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.ISidedModelLoader;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.rendering.IMeshData;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModel;
import finalforeach.cosmicreach.util.IGameTagList;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;

import java.util.Arrays;

public class PipeConnectorFunction implements ISidedBlockConnector.ConnectorFunction {

    BlockModelGenerator collisionModelGenerator;
    public final String collisionModelNameA;
    public final String collisionModelNameB;
    public final String defaultModelName;
    public final String itemModelName;
    private final String modelBaseName;

    final Identifier id;

    private static final String[] DIRECTIONS = new  String[]{
            "nx", "px", "ny", "py", "nz", "pz",
    };

    public PipeConnectorFunction(Identifier id, Identifier modelDirectory, String namePrefix, boolean hasItemModel) {
        this.id = id;
        this.defaultModelName = id.getNamespace() + "_" + id.getName();
        this.collisionModelNameA = this.defaultModelName + "_collsionA";
        this.collisionModelNameB = this.defaultModelName + "_collsionB";
        this.itemModelName = defaultModelName + "-item";
        this.modelBaseName = defaultModelName + "-" + namePrefix + "-";

        String modelPartialPath = modelDirectory.toString() + "/" + namePrefix;

        ISidedModelLoader modelLoader = ISidedModelLoader.getInstance();
        if (hasItemModel) {
            modelLoader.loadModel(itemModelName, GameAssetLoader.loadAsset(modelPartialPath + "-item.json").readString());
        }

        for (String direction : DIRECTIONS) {
            for (int i = 0; i < 2; i++) {
                String modelName = "-" + direction + "-o" + (i == 1 ? "n" : "ff");
                String modelPath = modelPartialPath + modelName + ".json";
                String modelFullName = modelBaseName + direction + "-" + i;

                modelLoader.loadModel(modelFullName, GameAssetLoader.loadAsset(modelPath).readString());
            }
        }

        this.collisionModelGenerator = new BlockModelGenerator(this.collisionModelNameA);
        this.collisionModelGenerator.createCuboid(Vector3.Zero, 16, 16, 16);
        this.collisionModelGenerator.isTransparent = true;

        modelLoader.loadModel(this.collisionModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);
        this.collisionModelGenerator.setName(this.collisionModelNameB);
        modelLoader.loadModel(this.collisionModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);
    }

    public static boolean canConnect(BlockState state) {
        IGameTagList list = state.getTags();
        if (list == null) return false;
        return list.contains(HorizonTags.TAG_ITEM_PIPE)
                || list.contains(HorizonTags.TAG_ITEM_EXTRACTOR)
                || list.contains(HorizonTags.TAG_ITEM_INSERTER)
                || list.contains(HorizonTags.TAG_POWER_HUB);
    }

    short[] pipeBlockLightLevels = new short[8];

    @Override
    public void connect(
            Zone zone, Chunk chunk, BlockState blockState,
            int x, int y, int z, IMeshData iMeshData,
            int opaqueBitmask, short[] blockLightLevels, int[] skyLightLevels
    ) {
        int powerLevel = blockState.getIntParam("powerLevel", 0);

        BlockState PX = zone.getBlockState(x + 1, y, z);
        BlockState NX = zone.getBlockState(x - 1, y, z);
        BlockState PY = zone.getBlockState(x, y + 1, z);
        BlockState NY = zone.getBlockState(x, y - 1, z);
        BlockState PZ = zone.getBlockState(x, y, z + 1);
        BlockState NZ = zone.getBlockState(x, y, z - 1);

        boolean PX_ON = PX != null && canConnect(PX);
        boolean NX_ON = NX != null && canConnect(NX);
        boolean PY_ON = PY != null && canConnect(PY);
        boolean NY_ON = NY != null && canConnect(NY);
        boolean PZ_ON = PZ != null && canConnect(PZ);
        boolean NZ_ON = NZ != null && canConnect(NZ);

        boolean useOffStates = true;

//        boolean PX_ON = false;
//        boolean NX_ON = false;
//        boolean PY_ON = false;
//        boolean NY_ON = false;
//        boolean PZ_ON = false;
//        boolean NZ_ON = false;

//        Arrays.fill(pipeBlockLightLevels, (short) (powerLevel << 11 | powerLevel << 5 | powerLevel));
        Arrays.fill(pipeBlockLightLevels, (short) ((short) powerLevel * 8));
//        Arrays.fill(pipeBlockLightLevels, (short) (powerLevel != 0 ? 63: 0));

        ISidedModelLoader modelLoader = ISidedModelLoader.getInstance();

        BlockModel side_px = modelLoader.loadModel(modelBaseName + "px-" + (PX_ON ? 1 : 0));
        if (PX_ON || useOffStates)
            side_px.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);
        BlockModel side_py = modelLoader.loadModel(modelBaseName + "py-" + (PY_ON ? 1 : 0));
        if (PY_ON || useOffStates)
            side_py.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);
        BlockModel side_pz = modelLoader.loadModel(modelBaseName + "pz-" + (PZ_ON ? 1 : 0));
        if (PZ_ON || useOffStates)
            side_pz.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);

        BlockModel side_nx = modelLoader.loadModel(modelBaseName + "nx-" + (NX_ON ? 1 : 0));
        if (NX_ON || useOffStates)
            side_nx.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);
        BlockModel side_ny = modelLoader.loadModel(modelBaseName + "ny-" + (NY_ON ? 1 : 0));
        if (NY_ON || useOffStates)
            side_ny.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);
        BlockModel side_nz = modelLoader.loadModel(modelBaseName + "nz-" + (NZ_ON ? 1 : 0));
        if (NZ_ON || useOffStates)
            side_nz.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);
    }

}
