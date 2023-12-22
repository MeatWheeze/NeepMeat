package com.neep.neepmeat.fluid_util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

import java.util.Iterator;

public class FluidBuffer<T extends FluidVariant> implements Storage<T>
{

    public NbtCompound writeNBT(NbtCompound compound)
    {
        return compound;
    }

    public void readNBT(NbtCompound compound)
    {
        
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public Iterator<StorageView<T>> iterator(TransactionContext transaction)
    {
        return null;
    }
}
