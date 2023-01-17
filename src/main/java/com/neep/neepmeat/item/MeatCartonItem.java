package com.neep.neepmeat.item;

import com.mojang.datafixers.util.Pair;
import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.processing.MeatFluidHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class MeatCartonItem extends BaseItem
{
    public MeatCartonItem(String registryName, TooltipSupplier tooltip, Settings settings)
    {
        super(registryName, tooltip, settings);
    }

    @Override
    public boolean isFood()
    {
        return true;
    }

    @Override
    public SoundEvent getEatSound()
    {
        return SoundEvents.ITEM_HONEYCOMB_WAX_ON;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.EAT;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        return super.use(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (this.isFood())
        {
            return eatFood(user, world, stack);
        }
        return stack;
    }

    // This is necessary because Item::getFoodComponent does not require the ItemStack as an argument, making it
    // impossible to return a custom FoodComponent based on the stack's NBT.
    protected ItemStack eatFood(LivingEntity user, World world, ItemStack stack)
    {
        if (stack.isFood())
        {
            world.emitGameEvent(user, GameEvent.EAT, user.getCameraBlockPos());
            world.playSound(null, user.getX(), user.getY(), user.getZ(), getEatSound(), SoundCategory.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            applyFoodEffects(user, stack, world);
            if (!(user instanceof PlayerEntity player && player.isCreative()))
            {
                stack.decrement(1);
            }

            // The poor man's polymorphism
            if (user instanceof PlayerEntity player)
            {
                NbtCompound nbt = stack.getOrCreateNbt();
                int food = (int) MeatFluidHelper.getHunger(nbt);
                float sat = MeatFluidHelper.getSaturation(nbt);
                player.getHungerManager().add(food, sat);
            }

            user.emitGameEvent(GameEvent.EAT);
        }
        return stack;
    }

    protected void applyFoodEffects(LivingEntity user, ItemStack stack, World world)
    {
        Item item = stack.getItem();
        if (item.isFood())
        {
            List<Pair<StatusEffectInstance, Float>> list = item.getFoodComponent().getStatusEffects();
            for (Pair<StatusEffectInstance, Float> pair : list)
            {
                if (world.isClient || pair.getFirst() == null || !(world.random.nextFloat() < pair.getSecond())) continue;
                user.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        NbtCompound nbt = itemStack.getNbt();
        float hunger = MeatFluidHelper.getHunger(nbt);
        float saturation = MeatFluidHelper.getSaturation(nbt);
        tooltip.add(new TranslatableText("item." + NeepMeat.NAMESPACE + ".meat_carton.hunger", hunger));
        tooltip.add(new TranslatableText("item." + NeepMeat.NAMESPACE + ".meat_carton.saturation", saturation));
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }
}
