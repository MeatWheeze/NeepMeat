package com.neep.neepmeat.machine.assembler;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class AssemblerStorage implements NbtSerialisable
{
    public static final int BUFFER_START = 12;
    public static final int BUFFER_END = 24;

    protected SimpleInventory inventory = new SimpleInventory(28)
    {
        @Override
        public void markDirty()
        {
            super.markDirty();
            parent.markDirty();
        }

        @Override
        public void readNbtList(NbtList nbtList)
        {
            int i;
            for (i = 0; i < this.size(); ++i)
            {
                this.setStack(i, ItemStack.EMPTY);
            }
            for (i = 0; i < nbtList.size(); ++i)
            {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 0xFF;
                if (j < 0 || j >= this.size()) continue;
                this.setStack(j, ItemStack.fromNbt(nbtCompound));
            }
        }

        @Override
        public NbtList toNbtList()
        {
            NbtList nbtList = new NbtList();
            for (int i = 0; i < this.size(); ++i)
            {
                ItemStack itemStack = this.getStack(i);
                if (itemStack.isEmpty()) continue;
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
            return nbtList;
        }
    };
    protected SyncableBlockEntity parent;
    protected int outputSlots = 0; // Each bit represents a slot

    public AssemblerStorage(SyncableBlockEntity parent)
    {
        this.parent = parent;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    public ItemStack findIngredient(ItemStack pattern)
    {
        int count = pattern.getCount();
        for (int i = BUFFER_START; i < BUFFER_END; ++i)
        {
            ItemStack foundStack = inventory.getStack(i);
            if (foundStack.isItemEqual(pattern) && foundStack.getCount() >= count)
            {
                foundStack.decrement(count);
                return pattern.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        nbt.putInt("outputSlots", outputSlots);
        NbtList invNbt = inventory.toNbtList();
        nbt.put("inventory", invNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.outputSlots = nbt.getInt("outputSlots");
        inventory.readNbtList(nbt.getList("inventory", 10));
    }
}
