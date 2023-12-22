package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.inventory.InventoryImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class RouterInventory implements InventoryImpl
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
        return InventoryImpl.super.removeStack(slot, count);
    }
}
