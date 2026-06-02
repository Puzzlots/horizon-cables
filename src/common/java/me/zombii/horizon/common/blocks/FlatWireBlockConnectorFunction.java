package me.zombii.horizon.common.blocks;

import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.BlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelFace;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.ISidedModelLoader;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.rendering.IMeshData;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModel;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;

public class FlatWireBlockConnectorFunction implements ISidedBlockConnector.ConnectorFunction {

    BlockModelGenerator parentModelGenerator;
    BlockModelGenerator childModelGenerator;
    final String parentModelName;
    public final String defaultModelName;

    final Identifier id;
    final Identifier texturePath;

    ModelCuboid cuboid;
    ModelFace face;

    public FlatWireBlockConnectorFunction(Identifier id) {
        this.id = id;
        this.texturePath = Identifier.of("horizon:textures/blocks/flatstone.png");
        this.defaultModelName = id.getNamespace() + "_" + id.getName();
        this.parentModelName = defaultModelName + "_texture_dict";
        this.parentModelGenerator = new BlockModelGenerator(this.parentModelName);
        this.parentModelGenerator.addTexture("all", this.texturePath);
        this.childModelGenerator = new BlockModelGenerator(this.parentModelGenerator, this.defaultModelName);
        this.childModelGenerator.isTransparent = true;
        ModelCuboid c = this.childModelGenerator.createCuboid(Vector3.Zero, 16f, 0f, 16f);
        c.setCullFace(false);
        c.setAO(false);
        c.min.y = .01f;
        c.setTextureIds("all");
        for (int i = 0; i < c.faces.length; i++) {
            if (i != 3) c.faces[i] = null;
        }

        this.cuboid = c;
        this.face = c.faces[3];

        ISidedModelLoader modelLoader = ISidedModelLoader.getInstance();

        modelLoader.loadModel(this.parentModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);
        modelLoader.loadModel(this.childModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);

        for (int i = 0; i <= 15; i++) createModel(i);
    }

    private void createModel(int i) {
        boolean PX_ON = (i & 1) != 0;
        boolean PZ_ON = (i & 2) != 0;
        boolean NX_ON = (i & 4) != 0;
        boolean NZ_ON = (i & 8) != 0;

        String modelName = getModelName(PX_ON, PZ_ON, NX_ON, NZ_ON);
        int minX = NX_ON ? 0 : 6;
        int minZ = NZ_ON ? 0 : 6;
        int maxX = PX_ON ? 16 : 10;
        int maxZ = PZ_ON ? 16 : 10;

        cuboid.min.x = minX;
        cuboid.min.z = minZ;
        cuboid.max.x = maxX;
        cuboid.max.z = maxZ;

        face.uv[0] = minX;
        face.uv[1] = minZ;
        face.uv[2] = maxX;
        face.uv[3] = maxZ;

        childModelGenerator.setName(modelName);
        ISidedModelLoader.getInstance().loadModel(childModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);
    }

    public static boolean canConnect(BlockState state) {
        return state.getBlockId().equals(FlatWireBlock.ID.toString());
    }

    public String getModelName(boolean px, boolean pz, boolean nx, boolean nz) {
        return this.defaultModelName + "_PX=" + (px ? 1 : 0) + "_PZ=" + (pz ? 1 : 0) + "_NX=" + (nx ? 1 : 0) + "_NZ=" + (nz ? 1 : 0);
    }

    @Override
    public void connect(
            Zone zone, Chunk chunk, BlockState blockState,
            int x, int y, int z, IMeshData iMeshData,
            int opaqueBitmask, short[] blockLightLevels, int[] skyLightLevels
    ) {
        BlockState PX = zone.getBlockState(x + 1, y, z);
        BlockState NX = zone.getBlockState(x - 1, y, z);
        BlockState PZ = zone.getBlockState(x, y, z + 1);
        BlockState NZ = zone.getBlockState(x, y, z - 1);

        boolean PX_ON = PX != null && canConnect(PX);
        boolean NX_ON = NX != null && canConnect(NX);
        boolean PZ_ON = PZ != null && canConnect(PZ);
        boolean NZ_ON = NZ != null && canConnect(NZ);

        String modelName = getModelName(PX_ON, PZ_ON, NX_ON, NZ_ON);

        BlockModel model = ISidedModelLoader.getInstance().loadModel(modelName);
        model.addVertices(iMeshData, x, y, z, opaqueBitmask, blockLightLevels, skyLightLevels);
    }

}
