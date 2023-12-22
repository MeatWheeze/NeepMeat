package com.neep.neepmeat.machine.assembler;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.inventory.GeneralInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.Direction;

public class AssemblerStorage implements NbtSerialisable
{
    public static final int BUFFER_START = 12;
    public static final int BUFFER_END = 24;
    public static final int INV_END = 28;

    protected GeneralInventory inventory = new GeneralInventory(28)
    {
        @Override
        public void markDirty()
        {
            super.markDirty();
            parent.markDirty();
        }
    };

    protected InventoryStorage inventoryStorage = InventoryStorage.of(inventory, null);
    Storage<ItemVariant> inBuffer = new CombinedStorage<>(inventoryStorage.getSlots().subList(BUFFER_START, BUFFER_END));
    Storage<ItemVariant> outBuffer = new CombinedStorage<>(inventoryStorage.getSlots().subList(BUFFER_END, INV_END));

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
            if (foundStack.isItemEqualIgnoreDamage(pattern) && foundStack.getCount() >= count)
            {
                foundStack.decrement(count);
                return pattern.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    // Indices 1-12
    public boolean isOutput(PropertyDelegate delegate, int index)
    {
        return ((delegate.get(0) >> index) & 1) == 1;
    }

    public Storage<ItemVariant> getStorage(Direction dir, boolean top)
    {
        if (top) return null;

        Direction facing = parent.getCachedState().get(AssemblerBlock.FACING);
        if (dir == facing)
        {
            return outBuffer;
        }
        return inBuffer;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("outputSlots", outputSlots);
        NbtList invNbt = inventory.toNbtList();
        nbt.put("inventory", invNbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.outputSlots = nbt.getInt("outputSlots");
        inventory.readNbtList(nbt.getList("inventory", 10));
    }
}
