package com.neep.meatweapons.mixin;

import com.neep.neepmeat.api.item.OverrideSwingItem;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin
{
    @Inject(method = "getHandSwingProgress", at = @At(value = "HEAD"), cancellable = true)
    private void overrideSwing(LivingEntity entity, float tickDelta, CallbackInfoReturnable<Float> cir)
    {
        if (entity.getMainHandStack().getItem() instanceof OverrideSwingItem overrideSwingItem)
        {
            cir.setReturnValue(0f);
            cir.cancel();
        }
    }
}
