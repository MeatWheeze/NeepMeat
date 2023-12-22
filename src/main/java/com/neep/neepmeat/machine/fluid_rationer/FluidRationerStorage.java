package com.neep.neepmeat.machine.fluid_rationer;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.HashSet;
import java.util.Set;

public class FluidRationerStorage extends WritableSingleFluidStorage
{
    SimpleInventory inventory;
    protected Set<FluidVariant> filters = new HashSet<>();

    public FluidRationerStorage(long capacity, Runnable finalCallback)
    {
        super(capacity, finalCallback);
        this.inventory = new SimpleInventory(1);
        inventory.addListener(sender ->
        {
            updateFilter(inventory.getStack(0));
            finalCallback.run();
        });
    }

    public void updateFilter(ItemStack stack)
    {
        filters.clear();
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        if (storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                for (StorageView<FluidVariant> view : storage)
                {
                    filters.add(view.getResource());
                }
                transaction.abort();
            }
        }
    }

    public boolean matchesFilter(FluidVariant variant)
    {
        return filters.isEmpty() || filters.contains(variant);
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt)
    {
        super.toNbt(nbt);
        nbt.put("inventory", this.inventory.toNbtList());
        return nbt;
    }

    @Override
    public NbtCompound readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.inventory.readNbtList(nbt.getList("inventory", NbtType.COMPOUND));
        return nbt;
    }
}
