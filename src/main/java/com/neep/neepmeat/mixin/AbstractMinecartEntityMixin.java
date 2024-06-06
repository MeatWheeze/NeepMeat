package com.neep.neepmeat.mixin;

import com.neep.neepmeat.block.SpecialRail;
import com.neep.neepmeat.interfaces.AbstractMinecartEntityAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin implements AbstractMinecartEntityAccess
{
    @Shadow protected abstract boolean willHitBlockAt(BlockPos pos);

    @Unique
    private Vec3d controllerVelocity = Vec3d.ZERO;

    // Passenger velocity is reset at some point after this is called.
    // We have to inject outside moveOnRail for compatibility with Audaki Cart Engine, which cancels moveOnRail immediately.
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;moveOnRail(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"),
        locals = LocalCapture.CAPTURE_FAILSOFT)
    void tick(CallbackInfo ci, int i, int j, int k, BlockPos blockPos, BlockState blockState)
    {
        AbstractMinecartEntity thisEntity = ((AbstractMinecartEntity) (Object) (this));
        Entity passenger = thisEntity.getFirstPassenger();
        if (passenger instanceof PlayerEntity player)
        {
            controllerVelocity = player.getVelocity();
        }

        if (blockState.getBlock() instanceof SpecialRail specialRail)
        {
            specialRail.apply(thisEntity.getWorld(), blockPos, blockState, thisEntity);
        }
    }

    @Override
    public Vec3d neepmeat$getControllerVelocity()
    {
        return controllerVelocity;
    }
}
