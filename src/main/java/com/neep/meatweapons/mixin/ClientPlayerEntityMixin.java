package com.neep.meatweapons.mixin;

import com.neep.meatweapons.entity.AbstractVehicleEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin
{
    @Shadow public Input input;
    @Shadow private boolean riding;

    @Inject(method = "tickRiding", at = @At("HEAD"))
    private void injectMethod(CallbackInfo ci)
    {
        if (((Entity) (Object) this).getVehicle() instanceof AbstractVehicleEntity vehicle)
        {
            vehicle.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
            this.riding |= this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack;
        }
    }
}
