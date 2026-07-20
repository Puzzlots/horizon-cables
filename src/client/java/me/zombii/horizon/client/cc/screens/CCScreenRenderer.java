package me.zombii.horizon.client.cc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.zombii.horizon.common.cc.display.ICCPalette;
import me.zombii.horizon.common.cc.display.ICCScreen;

import java.util.UUID;

public class CCScreenRenderer {

    private final Pixmap pixmap;
    private final Texture texture;
    private final ICCScreen screen;

    private static final Object2ObjectMap<UUID, CCScreenRenderer> renderers = new Object2ObjectOpenHashMap<>();

    public static CCScreenRenderer getOrNew(ICCScreen screen) {
        CCScreenRenderer renderer = renderers.get(screen);
        if (renderer == null) {
            renderer = new CCScreenRenderer(screen);
            renderers.put(screen.getUUID(), renderer);
        }
        return renderer;
    }

    public CCScreenRenderer(
            ICCScreen screen
    ) {
        this.pixmap = new Pixmap(screen.getWidth(), screen.getHeight(), Pixmap.Format.RGB565);
        this.texture = new Texture(screen.getWidth(), screen.getHeight(), Pixmap.Format.RGB565);
        this.screen = screen;
        screen.setOnSwap((c) -> writeToPix());
    }

    public Texture getTexture() {
        return texture;
    }

    public void writeToPix() {
        ICCPalette palette = screen.getPalette();
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        for (int x = 0; x < screen.getWidth(); x++) {
            for (int y = 0; y < screen.getHeight(); y++) {
                pixmap.drawPixel(x, y, palette.getColor(screen.getPixel(x, y)));
            }
        }
        System.out.println("Started " + pixmap.getPixel(0, 0) + " " + screen.getPixel(0, 0));
        Gdx.app.postRunnable(() -> {
            this.texture.draw(pixmap, 0, 0);
            System.out.println("Finished " + pixmap.getPixel(0, 0));
        });
    }

}
