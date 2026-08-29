package me.zombii.horizon.client.mixins;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import finalforeach.cosmicreach.rendering.entities.EntityDebugRenderer;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDebugRenderer.class)
public class DebugRenderMixin {

    @Shadow
    private static ShapeRenderer sr;

    @Inject(method = "drawEntityDebugBoundingBoxes", at = @At(shift = At.Shift.AFTER, value = "INVOKE", target = "Lcom/badlogic/gdx/graphics/glutils/ShapeRenderer;setProjectionMatrix(Lcom/badlogic/gdx/math/Matrix4;)V"))
    private static void add(Zone playerZone, Camera rawWorldCamera, CallbackInfo ci) {
    }

}
