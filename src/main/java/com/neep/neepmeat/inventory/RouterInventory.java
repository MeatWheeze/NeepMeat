package com.neep.neepmeat.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class RouterInventory implements ImplementedInventory
{
    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(18, ItemStack.EMPTY);

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
