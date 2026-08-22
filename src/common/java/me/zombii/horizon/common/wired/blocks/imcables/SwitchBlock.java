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
import finalforeach.cosmicreach.blocks.MissingBlockStateResult;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.BlockSetter;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.wired.be.energy.EnergyNetworkHubBlockEntity;
import me.zombii.horizon.common.wired.be.energy.SwitchBlockEntity;
import me.zombii.horizon.common.wired.network.AbstractNetwork;
import me.zombii.horizon.common.wired.network.AbstractNode;
import me.zombii.horizon.common.wired.network.NetworkManager;
import me.zombii.horizon.common.wired.network.energy.EnergyNetwork;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyBlock;
import me.zombii.horizon.common.wired.network.energy.interfaces.IEnergyHubBlockEntity;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNode;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeCable;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNodeSwitch;

import java.util.Arrays;
import java.util.List;

public class SwitchBlock implements IEnergyBlock {

    private static final Direction[] PORTS = {
            Direction.POS_X, Direction.POS_Z,
            Direction.NEG_X, Direction.NEG_Z,
            Direction.POS_Y, Direction.NEG_Y
    };

    private static final List<Direction> PORT_LIST = Arrays.asList(PORTS);

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "switch");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;

    public SwitchBlock() {
        this.blockGenerator = new BlockGenerator(ID);
        this.blockGenerator.setBlockEntity(SwitchBlockEntity.ID);

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "switch-block-events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);
        eventGenerator.inject(-1, "onLaserHit", this::onLaserHit);
//        eventGenerator.inject(-1, "onInteract", this::onInteract);

        State offState = this.blockGenerator.createState("default");
        offState.blockEventId = eventGenerator.getId();
        offState.modelId = "horizon:imcables/switch/switch-off.json";
        offState.isOpaque.set(true);
        offState.lightAttenuation = 0;
        offState.isCatalogHidden.set(false);
        offState.dropId = getId().toString() + "[default]";

        State onState = this.blockGenerator.createState("on");
        onState.blockEventId = eventGenerator.getId();
        onState.modelId = "horizon:imcables/switch/switch-on.json";
        onState.isOpaque.set(true);
        onState.lightAttenuation = 0;
        onState.isCatalogHidden.set(true);
        offState.dropId = getId().toString() + "[default]";
    }

//    @Override
//    public void onInteract(BlockEventArgs args) {
//        if (!GameSingletons.isHost()) return;
//
//        EnergyNetwork network = NetworkManager.findNetwork(
//                IEnergyBlock.class,
//                IEnergyHubBlockEntity.NETWORK_DISCOVERY_FUNCTION,
//                args.blockPos
//        );
//
//        if (network == null) return;
//
//        NetworkManager.build(network, args.blockPos, false);
//    }

    public void onLaserHit(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        EnergyNodeSwitch node = (EnergyNodeSwitch) ((SwitchBlockEntity)args.blockPos.getBlockEntity()).getNetwork()
                .getNode(
                        args.blockPos.getGlobalX(),
                        args.blockPos.getGlobalY(),
                        args.blockPos.getGlobalZ()
                );
        node.toggle();

        BlockState state = BlockState.getInstance("horizon:switch[" + (node.isPowered() ? "on" : "default") + "]", MissingBlockStateResult.MISSING_OBJECT);
        node.setState(state);
        BlockSetter.get().replaceBlock(args.zone, state, args.blockPos);
    }

    @Override
    public void onPlace(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;
    }

    public void onBreak(BlockEventArgs args) {
        if (!GameSingletons.isHost()) return;

        Direction direction = args.srcBlockState.getParamDirection("direction");
        EnergyNode selfNode = (EnergyNode) ((SwitchBlockEntity)args.blockPos.getBlockEntity()).getNetwork()
                .getNode(
                args.blockPos.getGlobalX(),
                args.blockPos.getGlobalY(),
                args.blockPos.getGlobalZ()
        );
        selfNode.powerOff(direction);
    }

    @Override
    public Direction[] getConnectionFaces(BlockState state) {
        return PORTS;
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction, BlockState target) {
        if (!PORT_LIST.contains(direction)) return false;

        IModBlock modBlock = BlockLoader.getModdedFromVanillaBlockGlobal(target.getBlock());
        if (modBlock instanceof SwitchBlock) return false;

        return target.tags.contains(HorizonTags.TAG_CABLE_CONNECTABLE);
    }

    @Override
    public AbstractNode createNode(AbstractNetwork network, IReadBlockPosition pos, BlockState state) {
        return new EnergyNodeSwitch(network, pos, state, this);
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        return new BlockEventGenerator[]{eventGenerator};
    }

    @Override
    public AbstractNode createEmptyNode() {
        return new EnergyNodeSwitch();
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
