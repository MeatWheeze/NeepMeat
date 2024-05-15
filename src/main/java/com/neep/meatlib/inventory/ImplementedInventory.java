package com.neep.meatlib.inventory;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public interface ImplementedInventory extends Inventory, NbtSerialisable
{
    static ImplementedInventory of(DefaultedList<ItemStack> items)
    {
        return () -> items;
    }

    static ImplementedInventory of(DefaultedList<ItemStack> items, Runnable callback)
    {
        return new ImplementedInventory()
        {
            @Override
            public DefaultedList<ItemStack> getItems()
            {
                return items;
            }

            @Override
            public void markDirty()
            {
                callback.run();
            }
        };
    }

    static ImplementedInventory ofSize(int size)
    {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    static ImplementedInventory ofSize(int size, Runnable callback)
    {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY), callback);
    }

    static void readNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks)
    {
        NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        // Inventories::readNbt does nothing if the received list is empty.
        if (nbtList.isEmpty())
        {
            stacks.clear();
            return;
        }

        for (int i = 0; i < nbtList.size(); ++i)
        {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j >= 0 && j < stacks.size())
            {
                stacks.set(j, ItemStack.fromNbt(nbtCompound));
            }
        }
    }

    DefaultedList<ItemStack> getItems();

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

    default float emptyStacks()
    {
        int filled = 0;
        for (int i = 0; i < size(); i++)
        {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty())
            {
                filled++;
            }
        }
        return size() - filled;
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
        if (!result.isEmpty())
        {
            markDirty();
        }
        return result;
    }

    @Override
    default ItemStack removeStack(int slot)
    {
        return removeStack(slot, getItems().get(slot).getCount());
    }

    @Override
    default void setStack(int slot, ItemStack stack)
    {
        getItems().set(slot, stack);
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

    default NbtCompound writeNbt(NbtCompound nbt)
    {
        Inventories.writeNbt(nbt, getItems());
        return nbt;
    }

    default void readNbt(NbtCompound tag)
    {
        readNbt(tag, getItems());
    }
}
