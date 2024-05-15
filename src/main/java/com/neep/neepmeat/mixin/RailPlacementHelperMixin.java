package com.neep.neepmeat.mixin;

import com.neep.neepmeat.block.PlayerControlTrack;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RailPlacementHelper.class)
public class RailPlacementHelperMixin
{
    @Shadow private BlockState state;

    @Inject(method = "computeRailShape", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    void onComputeRailShape(RailPlacementHelper placementHelper, CallbackInfo ci)
    {
        // Prevent exceptions due to normal rails trying to make a PlayerControlTrack into a slope
        if (state.getBlock() instanceof PlayerControlTrack)
        {
            ci.cancel();
        }
    }
}
