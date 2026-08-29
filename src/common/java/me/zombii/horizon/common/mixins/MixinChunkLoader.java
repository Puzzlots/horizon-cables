package me.zombii.horizon.common.mixins;

import finalforeach.cosmicreach.io.ChunkLoader;
import finalforeach.cosmicreach.util.SaveLocation;
import finalforeach.cosmicreach.world.World;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;

@Mixin(ChunkLoader.class)
public class MixinChunkLoader {

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private static void loadWorld(String worldFolderName, CallbackInfoReturnable<World> cir) {
        File worldLocation = new File(SaveLocation.getWorldSaveFolderLocation(worldFolderName));

        File file = new File(worldLocation, "networks.json");
        System.out.println("Loading world: " + file.getAbsolutePath() + " with networks");

        try {
            AbstractDataStorageDevice.loadComponents(worldLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
