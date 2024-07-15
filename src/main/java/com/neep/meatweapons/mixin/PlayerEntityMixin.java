package com.neep.meatweapons.mixin;

import com.neep.meatweapons.entity.HitOnCollideEntity;
import com.neep.meatweapons.entity.PlayerWeaponManager;
import com.neep.meatweapons.interfaces.MWPlayerEntity;
import com.neep.meatweapons.meatgun.module.HalberdModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements MWPlayerEntity, HitOnCollideEntity
{
    @Unique
    private int collideHitTicks;
    @Unique
    private float collideHitDamage;

    @Unique
    protected PlayerWeaponManager manager = new PlayerWeaponManager((PlayerEntity) (Object) this);

    @Override
    public PlayerWeaponManager meatweapons$getWeaponManager()
    {
        return manager;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    void onTick(CallbackInfo ci)
    {
        manager.tick();

        collideHitTicks = Math.max(0, collideHitTicks - 1);
    }

    @Override
    public void meatweapons$setActiveTicks(int ticks)
    {
        this.collideHitTicks = ticks;
    }

    @Override
    public void meatweapons$setDamage(float damage)
    {
        this.collideHitDamage = damage;
    }

    @Inject(method = "collideWithEntity", at = @At("HEAD"))
    private void onCollideWithEntity(Entity entity, CallbackInfo ci)
    {
        HalberdModule.onEntityCollide(((PlayerEntity) (Object) this), entity, collideHitTicks, collideHitDamage);
    }
}
