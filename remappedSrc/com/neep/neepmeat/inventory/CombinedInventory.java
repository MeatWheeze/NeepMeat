package com.neep.neepmeat.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class CombinedInventory implements Inventory
{
    protected Inventory[] inventories;
    protected int[] sizes;

    public CombinedInventory(Inventory... inventories)
    {
        this.inventories = inventories;
        this.sizes = new int[inventories.length];
        for (int i = 0; i < inventories.length; ++i)
        {
            sizes[i] = inventories[i].size();
        }
    }

    public int wrapSlot(int slot)
    {
        int total = 0;
        for (int size : sizes)
        {
            if (slot >= total && slot < total + size)
                return slot - total;
            total += size;
        }
        return -1;
    }

    public Inventory getInventory(int slot)
    {
        int total = 0;
        // Return if index is within the sub-inventory's range of indices
        for (Inventory inventory : inventories)
        {
            int size = inventory.size();
            if (slot >= total && slot < total + size)
                return inventory;
            total += size;
        }
        return null;
    }

    @Override
    public int size()
    {
        return Arrays.stream(sizes).sum();
    }

    @Override
    public boolean isEmpty()
    {
        return Arrays.stream(inventories).allMatch(Inventory::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return getInventory(slot).getStack(wrapSlot(slot));
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        return getInventory(slot).removeStack(wrapSlot(slot), amount);
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        return getInventory(slot).removeStack(wrapSlot(slot));
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        getInventory(slot).setStack(wrapSlot(slot), stack);
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clear()
    {
        Arrays.stream(inventories).forEach(Inventory::clear);
    }
}
