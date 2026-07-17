package me.zombii.horizon.common;

import me.zombii.horizon.common.screen.ScreenOpenInfo;

import java.util.concurrent.atomic.AtomicReference;

public interface IHorizonClientBound {

    AtomicReference<IHorizonClientBound> INSTANCE = new AtomicReference<>(new IHorizonClientBound() {});

    default void openScreen(ScreenOpenInfo info) {}

}
