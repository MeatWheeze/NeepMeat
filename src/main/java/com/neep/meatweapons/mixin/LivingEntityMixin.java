package com.neep.meatweapons.mixin;

import com.neep.meatweapons.entity.BulletDamageSource;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    // Intercepts the custom bullet DamageSource in order to prevent silly amounts of knockback
    @Shadow protected int playerHitTimer;
    @Shadow public float knockbackVelocity;
    @Shadow protected PlayerEntity attackingPlayer;

    @Shadow public abstract void setAttacker(@Nullable LivingEntity attacker);

    @Shadow public abstract boolean blockedByShield(DamageSource source);

    @Shadow protected abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow @Nullable private LivingEntity attacker;

    @Shadow protected abstract void applyDamage(DamageSource source, float amount);

    @Shadow protected float lastDamageTaken;

    @Shadow public abstract boolean isDead();

    @Shadow protected abstract boolean tryUseTotem(DamageSource source);

    @Shadow @Nullable protected abstract SoundEvent getDeathSound();

    @Shadow public abstract void onDeath(DamageSource source);

    @Shadow protected abstract float getSoundVolume();

    @Shadow public abstract float getSoundPitch();

    @Shadow protected abstract void playHurtSound(DamageSource source);

    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void injectMethod(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        // This is supposed to mirror the implementation of LivingEntity::damage()
        if (source instanceof BulletDamageSource bulletSource)
        {
//            cir.setReturnValue(false);
//            if (true) return;

            LivingEntity thisEntity = ((LivingEntity) (Object) this);

            if (((Entity) (Object) this).timeUntilRegen > 10.0f)
            {
                if (amount <= this.lastDamageTaken)
                {
                    cir.setReturnValue(false);
                    return;
                }
            }

            // Do shield things
            boolean shielded = false;
            float shieldedDamage = 0;
            if (amount > 0.0f && blockedByShield(source))
            {
                damageShield(amount);
                shieldedDamage = amount;
                shielded = true;
            }


            applyDamage(source, amount);
            lastDamageTaken = amount;
            ((Entity) (Object) this).timeUntilRegen = bulletSource.getRegenTime();

            Entity sourceEntity = bulletSource.getAttacker();
            if (sourceEntity instanceof LivingEntity livingAttacker)
            {
                setAttacker(livingAttacker);
                if (livingAttacker instanceof PlayerEntity player)
                {
                    playerHitTimer = 100;
                    attackingPlayer = player;
                }
            }

            // Not entirely sure what this does
            sendStatus(thisEntity, (EntityDamageSource) source, shielded);

            // Apply knockback
            if (sourceEntity != null)
                knockback(thisEntity, bulletSource, sourceEntity);

            playSounds(thisEntity, source);

            // Update stats
            updateStats(thisEntity, source, amount, shieldedDamage, shielded);

            cir.setReturnValue(true);
        }
    }

    protected void sendStatus(LivingEntity thisEntity, EntityDamageSource source, boolean shielded)
    {
        if (shielded)
        {
            // Shield bonk?
            thisEntity.getEntityWorld().sendEntityStatus(thisEntity, (byte) 29);
        }
        else if (source.isThorns())
        {
            // Thorns or something
            thisEntity.getEntityWorld().sendEntityStatus(thisEntity, (byte) 33);
        }
        else
        {
            // Generic damage?
            thisEntity.getEntityWorld().sendEntityStatus(thisEntity, (byte) 2);
        }
    }

    protected void updateStats(LivingEntity thisEntity, DamageSource source, float amount, float shieldedDamage, boolean shielded)
    {
        if (thisEntity instanceof ServerPlayerEntity)
        {
            Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity) thisEntity , source, amount, amount, shielded);
            if (shieldedDamage > 0.0f && shieldedDamage < 3.4028235E37f)
            {
                ((ServerPlayerEntity) thisEntity).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(shieldedDamage * 10.0f));
            }
        }
        if (attacker instanceof ServerPlayerEntity) {
            Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity) attacker, thisEntity, source, amount, amount, shielded);
        }
    }

    protected void knockback(LivingEntity thisEntity, BulletDamageSource bulletSource, Entity attacker)
    {
        double dx = attacker.getX() - thisEntity.getX();
        double dz = attacker.getZ() - thisEntity.getZ();
        while (dx * dx + dz * dz < 1.0E-4)
        {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
        }

        knockbackVelocity = (float)(MathHelper.atan2(dz, dx) * 57.2957763671875 - (double) thisEntity.getYaw());
        thisEntity.takeKnockback(bulletSource.getPunch(), dx, dz);
    }

    protected void playSounds(LivingEntity thisEntity, DamageSource source)
    {
        if (isDead())
        {
            if (!tryUseTotem(source))
            {
                SoundEvent deathSound = getDeathSound();
                if (deathSound != null)
                {
                    thisEntity.playSound(deathSound, getSoundVolume(), getSoundPitch());
                }
                onDeath(source);
            }
        }
        else
        {
            playHurtSound(source);
        }
    }

}
