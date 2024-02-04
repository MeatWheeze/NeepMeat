package com.neep.meatlib.recipe;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.DynamicRegistryManager;

/**
 * This class overrides some Vanilla recipe methods so that they do not have to be overridden in every subclass.
 */
public abstract class ImplementedRecipe<T extends ImplementedRecipe.DummyInventory> implements Recipe<T>
{
    @Override
    public ItemStack craft(T inventory)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        // TODO: Migrate to MeatlibRecipe
        return ItemStack.EMPTY;
    }

    public interface DummyInventory extends ImplementedInventory
    {
        @Override
        default DefaultedList<ItemStack> getItems()
        {
            return DefaultedList.of();
        }
    }
}
