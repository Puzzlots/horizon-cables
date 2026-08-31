package me.zombii.horizon.server.mixins;

import finalforeach.cosmicreach.networking.server.ServerZoneLoader;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.immersivecables.ImEventManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerZoneLoader.class)
public class ServerZoneLoaderMixin {

    @Shadow
    @Final
    public Zone zone;

    @Inject(method = "requestExit", at = @At("TAIL"))
    public void requestExit(CallbackInfo ci) {
        ImEventManager.resetZone(zone);
    }

}
