package com.neep.meatweapons.mixin;

import com.neep.meatweapons.interfaces.VehicleMovementHolder;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements VehicleMovementHolder
{
    @Unique private double vehicleMovementX;
    @Unique private double vehicleMovementY;
    @Unique private double vehicleMovementZ;

    @Inject(method = "onVehicleMove",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onOnVehicleMove(VehicleMoveC2SPacket packet, CallbackInfo ci, Entity entity, ServerWorld serverWorld, double d, double e, double f, double g, double h, double i, float j, float k, double l, double m, double n, double o, double p, boolean bl, boolean bl2)
    {
        vehicleMovementX = l;
        vehicleMovementY = m;
        vehicleMovementZ = n;
    }

    @Override
    public double meatweapons$movementX()
    {
        return vehicleMovementX;
    }

    @Override
    public double meatweapons$movementY()
    {
        return vehicleMovementY;
    }

    @Override
    public double meatweapons$movementZ()
    {
        return vehicleMovementZ;
    }
}
