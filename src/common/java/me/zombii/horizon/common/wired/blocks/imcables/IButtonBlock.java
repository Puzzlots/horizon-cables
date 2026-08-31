package me.zombii.horizon.common.wired.blocks.imcables;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.Trigger;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.BlockSetter;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.immersivecables.be.ButtonBE;
import me.zombii.horizon.immersivecables.be.SwitchBE;
import org.hjson.JsonArray;

public class IButtonBlock implements IModBlock {

    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "i-button-block");

    private final BlockGenerator blockGenerator;
    private final BlockEventGenerator eventGenerator;

    public IButtonBlock() {
        this.blockGenerator = new BlockGenerator(ID);
        this.blockGenerator.setBlockEntity(Identifier.of(ButtonBE.ID));
        JsonArray portArray = new JsonArray();
        for (Direction allDirection : Direction.ALL_DIRECTIONS) {
            portArray.add(allDirection.toString());
        }
        this.blockGenerator.getBlockEntityParams().put("ports", portArray);

        eventGenerator = new BlockEventGenerator(
                BlockEventGenerator.DEFAULT_BLOCK_EVENTS_ID,
                Identifier.of("horizon", "ibutton-block-events")
        );

        eventGenerator.inheritParentContents();

        eventGenerator.inject(-1, "onPlace", this::onPlace);
        eventGenerator.inject(0, "onBreak", this::onBreak);
        eventGenerator.inject(-1, "onTurnOn", (e) -> {
            BlockState state = e.blockPos.getBlockState().getVariantWithParam("on", true);
            BlockSetter.get().replaceBlock(state, (BlockPosition) e.blockPos);
        });
        eventGenerator.inject(-1, "onTurnOff", (e) -> {
            BlockState state = e.blockPos.getBlockState().getVariantWithParam("on", false);
            BlockSetter.get().replaceBlock(state, (BlockPosition) e.blockPos);
        });
        Trigger trigger = new Trigger("base:block_entity_signal");
        trigger.setParameter("signal", "toggle");
        eventGenerator.getOrCreateTriggerGroup("onLaserHit").insertTrigger(0, trigger);

//        eventGenerator.inject(-1, "onLaserHit", this::onLaserHit);
//        eventGenerator.inject(-1, "onInteract", this::onInteract);

        State offState = this.blockGenerator.createState("on=false");
        offState.blockEventId = eventGenerator.getId();
        offState.modelId = "horizon:imcables/button/button-off.json";
        offState.isOpaque.set(true);
        offState.lightAttenuation = 0;
        offState.isCatalogHidden.set(false);
        offState.dropId = getId().toString() + "[on=false]";

        State onState = this.blockGenerator.createState("on=true");
        onState.blockEventId = eventGenerator.getId();
        onState.modelId = "horizon:imcables/button/button-on.json";
        onState.isOpaque.set(true);
        onState.lightAttenuation = 0;
        onState.isCatalogHidden.set(true);
        onState.dropId = getId().toString() + "[on=false]";
    }

//    public void onLaserHit(BlockEventArgs args) {
//        if (!GameSingletons.isHost()) return;
//
//        EnergyNodeSwitch node = (EnergyNodeSwitch) ((SwitchBlockEntity)args.blockPos.getBlockEntity()).getNetwork()
//                .getNode(
//                        args.blockPos.getGlobalX(),
//                        args.blockPos.getGlobalY(),
//                        args.blockPos.getGlobalZ()
//                );
//        node.toggle();
//
//        BlockState state = BlockState.getInstance("horizon:switch[" + (node.isPowered() ? "on" : "default") + "]", MissingBlockStateResult.MISSING_OBJECT);
//        node.setState(state);
//        BlockSetter.get().replaceBlock(args.zone, state, args.blockPos);
//    }

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
