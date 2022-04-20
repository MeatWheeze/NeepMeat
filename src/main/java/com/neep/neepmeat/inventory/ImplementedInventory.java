package com.neep.neepmeat.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public interface ImplementedInventory extends Inventory
{
    DefaultedList<ItemStack> getItems();

    static ImplementedInventory of(DefaultedList<ItemStack> items)
    {
        return () -> items;
    }

    static ImplementedInventory ofSize(int size)
    {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    @Override
    default int size()
    {
        return getItems().size();
    }

    @Override
    default boolean isEmpty()
    {
        for (int i = 0; i < size(); i++)
        {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot)
    {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int count)
    {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    default ItemStack removeStack(int slot)
    {
//        return Inventories.removeStack(getItems(), slot);
        return removeStack(slot, getItems().get(slot).getCount());
    }

    @Override
    default void setStack(int slot, ItemStack stack)
    {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack())
        {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    default void clear()
    {
        getItems().clear();
    }

    @Override
    default void markDirty()
    {
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    default NbtCompound writeNbt(NbtCompound tag)
    {
        Inventories.writeNbt(tag, getItems());
        return tag;
    }

    default void readNbt(NbtCompound tag)
    {
        Inventories.readNbt(tag, getItems());
    }
}
