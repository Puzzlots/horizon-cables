package me.zombii.horizon.common.cc.blocks;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

public class BiosFlasherBlock implements IModBlock {

    public static final Identifier SCREEN_ID = Identifier.of(HorizonCommon.NAMESPACE, "bios-flasher-screen");
    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "horizon-bios-flasher-block");

    @Override
    public void onInteract(BlockEventArgs args) {
        if (GameSingletons.isHost()) {
            ScreenOpenInfo info = new ScreenOpenInfo(
                    args.srcPlayer,
                    SCREEN_ID,
                    args.blockPos,
                    null
            );
            ScreenManager.openScreen(info);
        }
    }

    @Override
    public BlockGenerator getGenerator() {
        BlockGenerator generator = new BlockGenerator(ID);
        State state = generator.createState("default");
        state.modelId = "base:models/blocks/model_debug.json";
        state.blockEventId = ID;
        return generator;
    }

    @Override
    public BlockEventGenerator[] getEventGenerators() {
        BlockEventGenerator generator = new BlockEventGenerator(ID);
        generator.inject(-1, "onInteract", this::onInteract);

        return new BlockEventGenerator[]{generator};
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
