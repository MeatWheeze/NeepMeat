package com.neep.neepmeat.inventory;

import com.neep.meatlib.inventory.InventoryImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class GeneralInventory implements InventoryImpl
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
        return InventoryImpl.super.removeStack(slot, count);
    }

    public void readNbtList(NbtList nbtList)
    {
        int i;
        for (i = 0; i < this.size(); ++i)
        {
            this.items.set(i, ItemStack.EMPTY);
        }
        for (i = 0; i < nbtList.size(); ++i)
        {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            if (j < 0 || j >= this.size()) continue;
            this.items.set(j, ItemStack.fromNbt(nbtCompound));
        }
    }

    public NbtList toNbtList()
    {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.size(); ++i)
        {
            ItemStack itemStack = this.items.get(i);
            if (itemStack.isEmpty()) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            itemStack.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }
}
