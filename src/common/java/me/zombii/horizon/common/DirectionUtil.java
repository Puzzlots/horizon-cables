package me.zombii.horizon.common;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.BlockSetter;

public class DirectionUtil {

    public static String toString(Direction direction) {
        return switch (direction) {
            case NEG_X -> "NegX";
            case POS_X -> "PosX";
            case NEG_Y -> "NegY";
            case POS_Y -> "PosY";
            case NEG_Z -> "NegZ";
            case POS_Z -> "PosZ";
        };
    }

    public static void flipOnSneak(BlockEventArgs args) {
        if (args.srcPlayer.isSneaking()) {
            Direction direction = args.srcBlockState.getParamDirection("direction");
            BlockState v = args.srcBlockState.getVariantWithParam(
                    "direction",
                    DirectionUtil.toString(direction.getOpposite()
                    ));
            BlockSetter.get().replaceBlock(v, (BlockPosition) args.blockPos);
        }
    }

}
