package me.zombii.horizon.client.mixins;

import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.immersivecables.ImEventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGame.class)
public class InGameMixin {

    @Inject(method = "unloadZone", at = @At("HEAD"))
    private void add(Zone z, CallbackInfo ci) {
        ImEventManager.resetZone(z);
    }

    @Inject(method = "unloadWorld", at = @At("HEAD"))
    private void unloadWorld(CallbackInfo ci) {
        ImEventManager.resetWorld();
    }

}
