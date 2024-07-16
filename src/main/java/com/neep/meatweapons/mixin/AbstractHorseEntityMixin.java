package com.neep.meatweapons.mixin;

import com.neep.meatweapons.meatgun.module.HalberdModule;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity
{
    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "tickControlled", at = @At("HEAD"))
    private void onTickControlled(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfo ci)
    {
        HalberdModule.onTickControlled(((AbstractHorseEntity) (Object) this), controllingPlayer, movementInput);
    }
}
