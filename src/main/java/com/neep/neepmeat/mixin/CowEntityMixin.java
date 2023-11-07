package com.neep.neepmeat.mixin;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public class CowEntityMixin
{
    @Inject(method = "interactMob", at = @At(value = "HEAD"))
    void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        NeepMeat.cowThingy((CowEntity) (Object) this, player, hand);
    }
}
