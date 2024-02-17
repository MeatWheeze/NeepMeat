package com.neep.meatlib.recipe;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public interface MeatlibRecipe<C> extends Recipe<ImplementedRecipe.DummyInventory>
{
    boolean matches(C context);

//    RecipeSerializer<?> getSerialiser();

    boolean takeInputs(C context, TransactionContext transaction);

    boolean ejectOutputs(C context, TransactionContext transaction);

//    MeatRecipeType<?> getType();

    MeatRecipeSerialiser<?> getSerializer();

    Identifier getId();

    @Override
    default ItemStack createIcon()
    {
        return ItemStack.EMPTY;
    }

    @Override
    default String getGroup()
    {
        return "neepmeat";
    }

    @Override
    default DefaultedList<Ingredient> getIngredients()
    {
        return DefaultedList.of();
    }

    @Override
    default boolean matches(ImplementedRecipe.DummyInventory inventory, World world)
    {
        return false;
    }

    @Override
    default ItemStack craft(ImplementedRecipe.DummyInventory inventory)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean fits(int width, int height)
    {
        return true;
    }

    @Override
    default DefaultedList<ItemStack> getRemainder(ImplementedRecipe.DummyInventory inventory)
    {
        return DefaultedList.of();
    }

    @Override
    default ItemStack getOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isIgnoredInRecipeBook()
    {
        return true;
    }

    @Override
    default boolean isEmpty()
    {
        return false;
    }
}
