package me.zombii.horizon.common.connectedblocks;

import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.connected.ISidedBlockConnector;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.enhanced.EnhancedBlockModelGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.ISidedModelLoader;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.rendering.IMeshData;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModel;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.network.INetworkedBlock;

import java.util.Arrays;

public class CableConnectorFunction implements ISidedBlockConnector.ConnectorFunction {

    EnhancedBlockModelGenerator childModelGenerator;
    public final String defaultModelName;

    final Identifier id;
    final INetworkedBlock<?> networkedBlock;

    public CableConnectorFunction(
            INetworkedBlock<?> networkedBlock,
            Identifier id,
            Identifier modelPath
    ) {
        this.id = id;
        this.networkedBlock = networkedBlock;

        this.defaultModelName = id.getNamespace() + "_" + id.getName();
        this.childModelGenerator = EnhancedBlockModelGenerator.fromEntityModelJsonAsPlanes(
                defaultModelName,
                GameAssetLoader.loadAsset(modelPath).readString(),
                false
        );
        this.childModelGenerator.isTransparent = true;
        this.childModelGenerator.cuboids.clear();
//        for (ModelCuboid cuboid : this.childModelGenerator.cuboids) {
//            cuboid.setCullFace(false);
//            cuboid.setAO(false);
//        }

        ISidedModelLoader modelLoader = ISidedModelLoader.getInstance();

        ModelCuboid cuboid = this.childModelGenerator.createCuboid(Vector3.Zero, new Vector3(16, 16, 16));
        this.childModelGenerator.getGroup("CENTER").cuboids.add(cuboid);
        Arrays.fill(cuboid.faces, null);

        modelLoader.loadModel(this.childModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);
        this.childModelGenerator.getGroup("CENTER").cuboids.remove(cuboid);
        this.childModelGenerator.cuboids.remove(cuboid);

        createModel(0b000000);
        createModel(0b100000);
        createModel(0b010000);
        createModel(0b001000);
        createModel(0b000100);
        createModel(0b000010);
        createModel(0b000001);
    }

    private void createModel(
            int i
    ) {
        boolean PX_ON = (i & 1) != 0;
        boolean NX_ON = (i & 2) != 0;
        boolean PY_ON = (i & 4) != 0;
        boolean NY_ON = (i & 8) != 0;
        boolean PZ_ON = (i & 16) != 0;
        boolean NZ_ON = (i & 32) != 0;

        childModelGenerator.getGroup("PX").setVisible(PX_ON);
        childModelGenerator.getGroup("NX").setVisible(NX_ON);
        childModelGenerator.getGroup("PY").setVisible(PY_ON);
        childModelGenerator.getGroup("NY").setVisible(NY_ON);
        childModelGenerator.getGroup("PZ").setVisible(PZ_ON);
        childModelGenerator.getGroup("NZ").setVisible(NZ_ON);
        childModelGenerator.getGroup("CENTER").setVisible(true);

        String modelName = getModelName(PX_ON, NX_ON, PY_ON, NY_ON, PZ_ON, NZ_ON);

        childModelGenerator.setName(modelName);
        ISidedModelLoader.getInstance().loadModel(childModelGenerator, ISidedModelLoader.DEFAULT_ROTATION);
    }

    public String getModelName(
            boolean px, boolean nx,
            boolean py, boolean ny,
            boolean pz, boolean nz
    ) {
        if (!(px || nx || py || ny || pz || nz))
            return defaultModelName + "_core";

        return this.defaultModelName
                + (px ? "_px" : "")
                + (nx ? "_nx" : "")
                + (py ? "_py" : "")
                + (ny ? "_ny" : "")
                + (pz ? "_pz" : "")
                + (nz ? "_nz" : "");
    }

    public boolean canConnect(BlockState state, Direction direction, BlockState target) {
        return this.networkedBlock.canConnect(state, direction, target);
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

        boolean PX_ON = PX != null && canConnect(blockState, Direction.POS_X, PX);
        boolean NX_ON = NX != null && canConnect(blockState, Direction.NEG_X, NX);
        boolean PY_ON = PY != null && canConnect(blockState, Direction.POS_Y, PY);
        boolean NY_ON = NY != null && canConnect(blockState, Direction.NEG_Y, NY);
        boolean PZ_ON = PZ != null && canConnect(blockState, Direction.POS_Z, PZ);
        boolean NZ_ON = NZ != null && canConnect(blockState, Direction.NEG_Z, NZ);

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

        BlockModel core = modelLoader.loadModel(defaultModelName + "_core");
        core.addVertices(iMeshData, x, y, z, opaqueBitmask, pipeBlockLightLevels, skyLightLevels);

        if (PX_ON) {
            BlockModel px = modelLoader.loadModel(defaultModelName + "_px");
            px.addVertices(
                    iMeshData, x, y, z, opaqueBitmask,
                    pipeBlockLightLevels, skyLightLevels
            );
        }
        if (PY_ON) {
            BlockModel py = modelLoader.loadModel(defaultModelName + "_py");
            py.addVertices(
                    iMeshData, x, y, z, opaqueBitmask,
                    pipeBlockLightLevels, skyLightLevels
            );
        }
        if (PZ_ON) {
            BlockModel pz = modelLoader.loadModel(defaultModelName + "_pz");
            pz.addVertices(
                    iMeshData, x, y, z, opaqueBitmask,
                    pipeBlockLightLevels, skyLightLevels
            );
        }
        if (NX_ON) {
            BlockModel nx = modelLoader.loadModel(defaultModelName + "_nx");
            nx.addVertices(
                    iMeshData, x, y, z, opaqueBitmask,
                    pipeBlockLightLevels, skyLightLevels
            );
        }
        if (NY_ON) {
            BlockModel ny = modelLoader.loadModel(defaultModelName + "_ny");
            ny.addVertices(
                    iMeshData, x, y, z, opaqueBitmask,
                    pipeBlockLightLevels, skyLightLevels
            );
        }
        if (NZ_ON) {
            BlockModel nz = modelLoader.loadModel(defaultModelName + "_nz");
            nz.addVertices(
                    iMeshData, x, y, z, opaqueBitmask,
                    pipeBlockLightLevels, skyLightLevels
            );
        }

    }

}
