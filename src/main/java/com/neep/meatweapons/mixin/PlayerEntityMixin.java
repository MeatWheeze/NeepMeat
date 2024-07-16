package com.neep.meatweapons.mixin;

import com.mojang.datafixers.util.Either;
import com.neep.meatweapons.entity.HitOnCollideEntity;
import com.neep.meatweapons.entity.PlayerWeaponManager;
import com.neep.meatweapons.interfaces.MWPlayerEntity;
import com.neep.meatweapons.meatgun.module.HalberdModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements MWPlayerEntity, HitOnCollideEntity
{
    @Shadow public abstract Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos);

    @Shadow public abstract void remove(RemovalReason reason);

    @Shadow public abstract boolean checkFallFlying();

    @Unique
    private int collideHitTicks;
    @Unique
    private float collideHitDamage;

    @Unique
    protected PlayerWeaponManager manager = new PlayerWeaponManager((PlayerEntity) (Object) this);

    public PlayerEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

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

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        // Invulnerability is necessary to make this attack useful.
        if (collideHitTicks > 0)
        {
            if (source.isOf(DamageTypes.PLAYER_ATTACK) || source.isOf(DamageTypes.MOB_ATTACK))
            {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
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
        HalberdModule.onEntityCollide((PlayerEntity) (Object) this, entity, collideHitTicks, collideHitDamage);
    }
}
