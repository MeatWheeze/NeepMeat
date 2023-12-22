package com.neep.neepmeat.api.storage;

import com.neep.neepmeat.fluid.MixableFluid;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
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

    protected boolean variantsCompatible(FluidVariant insertedVariant)
    {
        return insertedVariant.equals(variant)
                || MixableFluid.canVariantsMix(variant, insertedVariant) && insertedVariant.isOf(variant.getFluid());
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

        if ((variantsCompatible(insertedVariant) || variant.isBlank()) && canInsert(insertedVariant))
        {
            long insertedAmount = Math.min(maxAmount, getCapacity(insertedVariant) - amount);

            if (insertedAmount > 0)
            {
                updateSnapshots(transaction);

                if (variant.isBlank())
                {
                    variant = insertedVariant;
                    amount = insertedAmount;
                }
                else
                {
                    if (MixableFluid.canVariantsMix(variant, insertedVariant))
                    {
                       variant = ((MixableFluid) variant.getFluid()).mixNbt(variant, amount, insertedVariant, insertedAmount);
                    }

                    amount += insertedAmount;
                }

                return insertedAmount;
            }
        }
        return 0;
    }

    protected void onFinalCommit()
    {
        if (finalCallback != null)
            finalCallback.run();
    }

    public NbtCompound toNbt(NbtCompound nbt)
    {
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        nbt.put(KEY_RESOURCE, getResource().toNbt());
        nbt.putLong(KEY_AMOUNT, amount);
    }

    public NbtCompound readNbt(NbtCompound nbt)
    {
        this.variant = FluidVariant.fromNbt((NbtCompound) nbt.get(KEY_RESOURCE));
        this.amount = nbt.getLong(KEY_AMOUNT);
        return nbt;
    }
}
