package com.neep.neepmeat.mixin;

import com.neep.neepmeat.interfaces.ILivingEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements ILivingEntity
{
    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    public boolean dropsLoot = true;

    @Override
    public void setDropsLoot(boolean bl)
    {
        this.dropsLoot = bl;
    }

    @Inject(method = "shouldDropLoot", at = @At(value = "HEAD"), cancellable = true)
    public void dropsLoot(CallbackInfoReturnable<Boolean> cir)
    {
        if (!dropsLoot) cir.setReturnValue(false);
    }
}
