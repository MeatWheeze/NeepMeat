package com.neep.meatlib.mixin;

import com.neep.meatlib.api.event.UseAttackCallback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Inject(method = "doItemUse", at = @At(value = "HEAD"), cancellable = true)
    void onItemUse(CallbackInfo ci)
    {
        if (!UseAttackCallback.DO_USE.invoker().context(MinecraftClient.getInstance())) ci.cancel();
    }

    @Inject(method = "doAttack", at = @At(value = "HEAD"), cancellable = true)
    void onAttack(CallbackInfoReturnable<Boolean> cir)
    {
        if (!UseAttackCallback.DO_ATTACK.invoker().context(MinecraftClient.getInstance()))
        {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At(value = "HEAD"), cancellable = true)
    void onAttack(boolean bl, CallbackInfo ci)
    {
        if (!UseAttackCallback.DO_ATTACK.invoker().context(MinecraftClient.getInstance()))
        {
            ci.cancel();
        }
    }

//    @Inject(method = )
//    void thing()
//    {
//
//    }

}
