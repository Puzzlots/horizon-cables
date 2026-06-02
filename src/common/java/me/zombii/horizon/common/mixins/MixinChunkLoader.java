package me.zombii.horizon.common.mixins;

import finalforeach.cosmicreach.io.ChunkLoader;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.SaveLocation;
import finalforeach.cosmicreach.world.World;
import me.zombii.horizon.common.network.NetworkGroup;
import me.zombii.horizon.common.network.NetworkGroups;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;

@Mixin(ChunkLoader.class)
public class MixinChunkLoader {

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private static void loadWorld(String worldFolderName, CallbackInfoReturnable<World> cir) {
        File file = new File(SaveLocation.getWorldSaveFolderLocation(worldFolderName) + "/networks.json");
        System.out.println("Loading world: " + file.getAbsolutePath() + " with networks");
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                byte[] data = stream.readAllBytes();
                stream.close();

                String contents = new String(data);
                JsonObject groups = JsonValue.readHjson(contents).asObject();
                for (Identifier name : NetworkGroups.GROUP_REGISTRY.names()) {
                    String strName = name.toString();
                    JsonValue groupData = groups.get(strName);
                    if (groupData != null && groupData.isObject()) {
                        JsonObject groupDataObject = groupData.asObject();
                        NetworkGroup<?> group = NetworkGroups.GROUP_REGISTRY.get(name);

                        group.load(groupDataObject);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
