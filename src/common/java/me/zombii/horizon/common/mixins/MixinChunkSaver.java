package me.zombii.horizon.common.mixins;

import finalforeach.cosmicreach.io.ChunkSaver;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.SaveLocation;
import finalforeach.cosmicreach.world.World;
import me.zombii.horizon.common.network.NetworkGroup;
import me.zombii.horizon.common.network.NetworkGroups;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Mixin(ChunkSaver.class)
public class MixinChunkSaver {

    @Inject(method = "saveWorldInfo", at = @At("HEAD"))
    private static void saveWorldInfo(World world, boolean overwrite, CallbackInfo ci) {
        File file = new File(SaveLocation.getWorldSaveFolderLocation(world.worldFolderName) + "/networks.json");
        System.out.println("Saving world: " + file.getAbsolutePath() + " with networks");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            JsonObject groups = new JsonObject();
            for (Identifier name : NetworkGroups.GROUP_REGISTRY.names()) {
                JsonObject groupData = new JsonObject();
                NetworkGroup<?> group = NetworkGroups.GROUP_REGISTRY.get(name);

                group.save(groupData);
                groups.set(name.toString(), groupData);
            }

            FileOutputStream stream = new FileOutputStream(file);
            stream.write(groups.toString(Stringify.FORMATTED).getBytes());
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
