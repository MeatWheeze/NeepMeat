package com.neep.meatlib.recipe;

import com.neep.neepmeat.inventory.ImplementedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;

/**
 * This class overrides some Vanilla recipe methods so that they do not have to be overridden in every subclass.
 */
public abstract class ImplementedRecipe<T extends ImplementedRecipe.DummyInventory> implements Recipe<T>
{
    @Override
    public ItemStack craft(T inventory)
    {
        throw new UnsupportedOperationException("Vanilla crafting methods are not supported");
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        throw new UnsupportedOperationException("Vanilla crafting methods are not supported");
    }

    public interface DummyInventory extends ImplementedInventory
    {
        @Override
        default DefaultedList<ItemStack> getItems()
        {
            throw new UnsupportedOperationException("This object is not a real inventory");
        }
    }
}
