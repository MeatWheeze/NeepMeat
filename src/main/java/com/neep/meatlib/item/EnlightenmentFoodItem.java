package com.neep.meatlib.item;

import com.neep.neepmeat.init.NMComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EnlightenmentFoodItem extends BaseCraftingItem
{
    public EnlightenmentFoodItem(String registryName, int loreLines, Settings settings)
    {
        super(registryName, loreLines, settings);
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
