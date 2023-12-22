package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkCartonItem extends BaseItem
{
    public MilkCartonItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (user instanceof ServerPlayerEntity serverPlayerEntity)
        {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        if (user instanceof PlayerEntity && !((PlayerEntity)user).getAbilities().creativeMode)
        {
            stack.decrement(1);
        }
        if (!world.isClient)
        {
            user.clearStatusEffects();
        }
        if (stack.isEmpty())
        {
            return new ItemStack(Items.BUCKET);
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
