package me.zombii.horizon.common.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlotContainer.class)
public class SlotContainerMixin {

    @Definition(id = "slot", local = @Local(name = "slot", ordinal = 1, type = ItemSlot.class))
    @Definition(id = "getItemStack", method = "Lfinalforeach/cosmicreach/items/ItemSlot;getItemStack()Lfinalforeach/cosmicreach/items/ItemStack;")
    @Expression("slot.getItemStack() == null")
    @ModifyExpressionValue(
            method = "addOrMergeFrom(Lfinalforeach/cosmicreach/items/ItemSlot;II)Z",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean merge3(
            boolean original,
            @Local(name = "slot", ordinal = 1, type = ItemSlot.class) ItemSlot slot,
            @Local(name = "fromSlot", ordinal = 0, type = ItemSlot.class) ItemSlot fromSlot
    ) {
        return original && slot.allowedToInput(fromSlot.getItemStack());
    }

    @Definition(id = "slot", local = @Local(name = "slot", ordinal = 1, type = ItemSlot.class))
    @Definition(id = "hasItemStack", method = "Lfinalforeach/cosmicreach/items/ItemSlot;hasItemStack()Z")
    @Expression("slot.hasItemStack()")
    @ModifyExpressionValue(
            method = "addOrMergeFrom(Lfinalforeach/cosmicreach/items/ItemSlot;IILjava/util/function/Predicate;)Z",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean merge4(
            boolean original,
            @Local(name = "slot", ordinal = 1, type = ItemSlot.class) ItemSlot slot,
            @Local(name = "fromSlot", ordinal = 0, type = ItemSlot.class) ItemSlot fromSlot
    ) {
        if (original) return true;

        return !slot.allowedToInput(fromSlot.getItemStack());
    }

}
