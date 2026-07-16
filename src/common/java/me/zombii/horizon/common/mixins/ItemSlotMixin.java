package me.zombii.horizon.common.mixins;

import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemSlot.class)
public abstract class ItemSlotMixin {

    @Shadow
    public abstract boolean allowedToInput(ItemStack itemStack);

    @Shadow
    public abstract ItemStack getItemStack();

    @Inject(method = "merge", at = @At("HEAD"), cancellable = true)
    private void merge(ItemStack stackFrom, CallbackInfoReturnable<Boolean> cir) {
        if (!this.allowedToInput(stackFrom)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "addItemStack*", at = @At("HEAD"), cancellable = true)
    private void merge2(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!this.allowedToInput(itemStack)) {
            cir.setReturnValue(false);
        }
    }

}
