package me.zombii.horizon.immersivecables;

import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.IBlockEntity;
import finalforeach.cosmicreach.util.constants.Direction;

public interface IEnergyBE extends IBlockEntity {

    Direction[] getPorts();

    boolean canConnect(BlockState state, BlockState target, BlockEntity beTarget, Direction direction);
    boolean isOn();

    default void initPorts() {}

    default boolean canConnect(Direction direction) {
        int gX = getGlobalX() + direction.getXOffset();
        int gY = getGlobalY() + direction.getYOffset();
        int gZ = getGlobalZ() + direction.getZOffset();

        BlockState target = ((BlockEntity)this).getZone().getBlockState(gX, gY, gZ);
        BlockEntity targetBE = ((BlockEntity) this).getZone().getBlockEntity(gX, gY, gZ);
        if (target == null) return false;

        boolean a = canConnect(getBlockState(), target, targetBE, direction);
        if (!a) return false;
        if (targetBE instanceof IEnergyBE) {
            return ((IEnergyBE) targetBE).canConnect(target, getBlockState(), ((BlockEntity) this), direction.getOpposite());
        }
        return true;
    }

    default void turnOn(Direction direction) {
        if (isOn()) return;
        doTurnOn(direction);
    }
    default void turnOff(Direction direction) {
        if (!isOn()) return;
        doTurnOff(direction);
    }

    void doTurnOff(Direction direction);
    void doTurnOn(Direction direction);

}
