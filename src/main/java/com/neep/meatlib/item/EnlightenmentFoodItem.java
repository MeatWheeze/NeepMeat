package com.neep.meatlib.item;

import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EnlightenmentFoodItem extends BaseCraftingItem
{
    public EnlightenmentFoodItem(RegistrationContext ctx, int loreLines, Settings settings)
    {
        super(ctx, loreLines, settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (this.isFood())
        {
            var enlightenment = NMComponents.ENLIGHTENMENT_MANAGER.getNullable(user);
            if (enlightenment != null)
            {
                enlightenment.addChronic(0.1f);
            }

            return user.eatFood(world, stack);
        }

        return super.finishUsing(stack, world, user);
    }
}
