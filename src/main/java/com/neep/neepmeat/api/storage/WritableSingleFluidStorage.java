package com.neep.neepmeat.api.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("UnstableApiUsage")
public class WritableSingleFluidStorage extends SingleVariantStorage<FluidVariant>
{
    public static final String KEY_RESOURCE = "resource";
    public static final String KEY_AMOUNT = "amount";

    protected long capacity;
    public float renderLevel;
    public Runnable finalCallback;

    public WritableSingleFluidStorage(long capacity, Runnable finalCallback)
    {
        this(capacity);
        this.finalCallback = finalCallback;
    }

    public WritableSingleFluidStorage(long capacity)
    {
        this.capacity = capacity;
        this.finalCallback = () -> {};
    }

    @Override
    protected FluidVariant getBlankVariant()
    {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant)
    {
        return capacity;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
    {
        return super.insert(insertedVariant, maxAmount, transaction);
    }

    protected void onFinalCommit()
    {
        if (finalCallback != null)
            finalCallback.run();
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put(KEY_RESOURCE, getResource().toNbt());
        nbt.putLong(KEY_AMOUNT, amount);
        return nbt;
    }

    public NbtCompound readNbt(NbtCompound nbt)
    {
        this.variant = FluidVariant.fromNbt((NbtCompound) nbt.get(KEY_RESOURCE));
        this.amount = nbt.getLong(KEY_AMOUNT);
        return nbt;
    }
}
