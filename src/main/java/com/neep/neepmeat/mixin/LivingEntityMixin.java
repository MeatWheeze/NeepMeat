package com.neep.neepmeat.mixin;

import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.interfaces.ILivingEntity;
import com.neep.neepmeat.item.EssentialSaltesItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
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
            LivingEntity thisLiving = (LivingEntity) (Object) this;
            World world = thisLiving.getWorld();
            Vec3d pos = thisLiving.getPos();
            ItemStack stack = NMItems.ESSENTIAL_SALTES.getDefaultStack();
            EssentialSaltesItem.storeEntity(stack, thisLiving);
            ItemEntity entity = new ItemEntity(world, pos.x, pos.y, pos.z, stack);
            world.spawnEntity(entity);
        }
    }
}
