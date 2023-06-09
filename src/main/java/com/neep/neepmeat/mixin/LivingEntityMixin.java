package com.neep.neepmeat.mixin;

import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.interfaces.ILivingEntity;
import com.neep.neepmeat.item.EssentialSaltesItem;
import com.neep.neepmeat.player.upgrade.PlayerUpgradeManager;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity
{
    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow protected abstract void dropInventory();

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Shadow protected abstract void playBlockFallSound();

    @Shadow public abstract Vec3d applyMovementInput(Vec3d movementInput, float slipperiness);

    @Shadow public abstract void setOnGround(boolean onGround);

    @Unique public boolean neepmeat$dropsLoot = true;

    @Override
    public void setDropsLoot(boolean bl)
    {
        this.neepmeat$dropsLoot = bl;
    }

    @Inject(method = "shouldDropLoot", at = @At(value = "HEAD"), cancellable = true)
    public void dropsLoot(CallbackInfoReturnable<Boolean> cir)
    {
        if (!neepmeat$dropsLoot) cir.setReturnValue(false);
    }

    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci)
    {
        if (hasStatusEffect(NMStatusEffects.ASH_PEPARATION))
        {
            EssentialSaltesItem.onEntityDeath((LivingEntity) (Object) this);
        }
    }
    @Inject(method = "applyEnchantmentsToDamage", at = @At(value = "TAIL"), cancellable = true)
    public void applyEnchantments(DamageSource source, float amount, CallbackInfoReturnable<Float> cir)
    {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player)
        {
            PlayerUpgradeManager upgradeManager = PlayerUpgradeManager.get(player);

            // Get total protection from enchantments and upgrades
            float protection = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), source)
                    + upgradeManager.getProtectionAmount(source, amount);
            float newAmount = DamageUtil.getInflictedDamage(amount, protection + protection);

            // Replace the vanilla protection amount with this one. This may conflict with other mods. Sorry.
            cir.setReturnValue(newAmount);
            cir.cancel();
        }
    }
}
