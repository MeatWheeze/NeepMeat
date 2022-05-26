package com.neep.neepmeat.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class GeneralInventory implements ImplementedInventory
{
    protected DefaultedList<ItemStack> items;

    public GeneralInventory(int size)
    {
        this.items = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        return items;
    }

    @Override
    public ItemStack removeStack(int slot, int count)
    {
        return ImplementedInventory.super.removeStack(slot, count);
    }
}
