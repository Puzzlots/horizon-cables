package me.zombii.horizon.common.wired.blocks.imcables;

import com.badlogic.gdx.math.Vector3;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.model.ModelCuboid;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.loading.BlockLoader;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.IReadBlockPosition;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.constants.Direction;
import me.zombii.horizon.common.DirectionUtil;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.NetworkManager;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyBlock;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyHubBlockEntity;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNode;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeDiode;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeInverter;

import java.util.Arrays;

public class DiodeBlock implements IEnergyBlock {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "diode");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;

    public DiodeBlock() {
        this.blockGenerator = new BlockGenerator(ID);

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "diode-block-events")
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
        defaultProperties.modelId = "horizon:imcables/diode.json";
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

        EnergyNetwork network = NetworkManager.findNetwork(
                IEnergyBlock.class,
                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
                args.blockPos
        );

        if (network == null) return;

        NetworkManager.build(network, args.blockPos, false);

        Direction direction = args.srcBlockState.getParamDirection("direction");
        EnergyNode selfNode = (EnergyNode) network.getNode(
                args.blockPos.getGlobalX(),
                args.blockPos.getGlobalY(),
                args.blockPos.getGlobalZ()
        );
        AbstractNode backwardNode = network.getNode(
                args.blockPos.getGlobalX() - direction.getXOffset(),
                args.blockPos.getGlobalY() - direction.getYOffset(),
                args.blockPos.getGlobalZ() - direction.getZOffset()
        );
        if (!(backwardNode instanceof EnergyNode eNode2)) return;
        if (eNode2.isPowered()) {
            selfNode.powerOn(direction);
        } else {
            selfNode.powerOff(direction);
        }
    }

    public void onBreak(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        EnergyNetwork network = NetworkManager.findNetwork(
                IEnergyBlock.class,
                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
                args.blockPos
        );

        if (network == null) return;

        Direction direction = args.srcBlockState.getParamDirection("direction");
        EnergyNode selfNode = (EnergyNode) network.getNode(
                args.blockPos.getGlobalX(),
                args.blockPos.getGlobalY(),
                args.blockPos.getGlobalZ()
        );
        selfNode.powerOff(direction);
        network.removeNode(args.blockPos);
    }

    @Override
    public Direction[] getConnectionFaces(BlockState state) {
        Direction direction = state.getParamDirection("direction");
        return new Direction[]{direction, direction.getOpposite()};
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction, BlockState target) {
        Direction dir = state.getParamDirection("direction");
        if (dir == direction || dir.getOpposite() == direction)
            return target.tags.contains(HorizonTags.TAG_CABLE_CONNECTABLE);

        return false;
    }

    @Override
    public AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        return new EnergyNodeDiode(network, pos, state, this);
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{eventGenerator};
    }

    @Override
    public AbstractNode createEmptyNode() {
        return new EnergyNodeDiode();
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
            value.tags.add(HorizonTags.TAG_STOP_PISTON_PUSH);
            value.tags.add(HorizonTags.TAG_STOP_PISTON_PULL);
        }
    }
}
