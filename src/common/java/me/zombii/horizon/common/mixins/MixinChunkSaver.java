package me.zombii.horizon.common.mixins;

import finalforeach.cosmicreach.io.ChunkSaver;
import finalforeach.cosmicreach.util.SaveLocation;
import finalforeach.cosmicreach.world.World;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(ChunkSaver.class)
public class MixinChunkSaver {

    @Inject(method = "saveWorldInfo", at = @At("HEAD"))
    private static void saveWorldInfo(World world, boolean overwrite, CallbackInfo ci) {
        File worldLocation = new File(SaveLocation.getWorldSaveFolderLocation(world.worldFolderName));

        File file = new File(worldLocation,  "networks.json");
        System.out.println("Saving world: " + file.getAbsolutePath() + " with networks");
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            AbstractDataStorageDevice.saveComponents(worldLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
