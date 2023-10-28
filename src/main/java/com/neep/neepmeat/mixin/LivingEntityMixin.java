package com.neep.neepmeat.mixin;

import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.interfaces.ILivingEntity;
import com.neep.neepmeat.item.EssentialSaltesItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
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
//        if (hasStatusEffect(NMStatusEffects.ASH_PEPARATION) && source.isIn(DamageTypeTags.IS_FIRE))
        if (hasStatusEffect(NMStatusEffects.ASH_PEPARATION) && source.isFire())
        {
            EssentialSaltesItem.onEntityDeath((LivingEntity) (Object) this);
        }
    }

//    @Inject(method = "applyEnchantmentsToDamage", at = @At(value = "TAIL"), cancellable = true)
//    public void applyEnchantments(DamageSource source, float amount, CallbackInfoReturnable<Float> cir)
//    {
//        if ((LivingEntity) (Object) this instanceof PlayerEntity player)
//        {
//            PlayerUpgradeManager upgradeManager = PlayerUpgradeManager.get(player);
//
//            // Get total protection from enchantments and upgrades
//            float protection = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), source)
//                    + upgradeManager.getProtectionAmount(source, amount);
//            float newAmount = DamageUtil.getInflictedDamage(amount, protection + protection);
//
//            // Replace the vanilla protection amount with this one. This may conflict with other mods. Sorry.
//            cir.setReturnValue(newAmount);
//            cir.cancel();
//        }
//    }
}
