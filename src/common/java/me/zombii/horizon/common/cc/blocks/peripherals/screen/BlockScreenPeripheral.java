package me.zombii.horizon.common.cc.blocks.peripherals.screen;

import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.BlockGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.state.State;
import finalforeach.cosmicreach.util.Identifier;
import me.zombii.horizon.common.HorizonCommon;

public class BlockScreenPeripheral implements IModBlock {

    public static final Identifier SCREEN_ID = Identifier.of(HorizonCommon.NAMESPACE, "monochrome-screen-peripheral");
    public static final Identifier ID = Identifier.of(HorizonCommon.NAMESPACE, "monochrome-screen-peripheral");
    public static final Identifier BE_ID = Identifier.of(HorizonCommon.NAMESPACE, "monochrome-screen-peripheral-be");

    @Override
    public BlockGenerator getGenerator() {
        BlockGenerator generator = new BlockGenerator(ID);
        generator.setBlockEntity(BE_ID);
        State state = generator.createState("default");
        state.modelId = "base:models/blocks/model_debug.json";
        return generator;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
