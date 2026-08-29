package me.zombii.horizon.common.cc.display;

public enum ScreenType {
    MONOCHROME, // 0-255 brightness per pixel, allows changing pixel on color

    RGB565_PALETTED_4, // Color screen with 15 colors 1 black, RGB565 palette
    RGB565_PALETTED_8, // Color screen with 255 colors 1 black, RGB565 palette

}