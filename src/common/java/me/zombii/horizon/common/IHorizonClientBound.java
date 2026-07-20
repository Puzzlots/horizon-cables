package me.zombii.horizon.common;

import finalforeach.cosmicreach.blocks.BlockPosition;
import me.zombii.horizon.common.cc.display.ICCScreen;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

import java.util.concurrent.atomic.AtomicReference;

public interface IHorizonClientBound {

    AtomicReference<IHorizonClientBound> INSTANCE = new AtomicReference<>(new IHorizonClientBound() {});

    default void openScreen(ScreenOpenInfo info) {}

}
