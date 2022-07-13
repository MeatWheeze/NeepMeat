package com.neep.neepmeat.machine.mixer;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MixerRecipe implements Recipe<MixerInventory>
{
    @Override
    public boolean matches(MixerInventory inventory, World world)
    {
        return false;
    }

    @Override
    public ItemStack craft(MixerInventory inventory)
    {
        return null;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        return null;
    }

    @Override
    public Identifier getId()
    {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return null;
    }

    @Override
    public RecipeType<?> getType()
    {
        return null;
    }
}
