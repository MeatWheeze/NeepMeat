package com.neep.neepmeat.fluid_transfer.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;

import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class TypedFluidBuffer extends WritableFluidBuffer implements Storage<FluidVariant>, SingleSlotStorage<FluidVariant>
{
    protected Predicate<FluidVariant> validTypes;
    protected Mode mode;

    public TypedFluidBuffer(BlockEntity parent, long capacity, Predicate<FluidVariant> validTypes, Mode mode)
    {
        super(parent, capacity);
        this.validTypes = validTypes;
        this.mode = mode;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (!mode.canInsert())
            return 0;

        return insertDirect(resource, maxAmount, transaction);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (!mode.canExtract())
            return 0;

        return extractDirect(resource, maxAmount, transaction);
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public Mode getMode()
    {
        return mode;
    }

    public long insertDirect(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {

        if (!validTypes.test(resource))
            return 0;

        if (getResource() == null || getResource().isBlank() || getAmount() <= 0)
        {
            this.amount = 0;
            this.resource = resource;
        }

        long inserted = Math.min(maxAmount, getCapacity() - getAmount());

        if (getResource().equals(resource) && inserted > 0)
        {
            this.updateSnapshots(transaction);
            amount += inserted;
            return inserted;
        }
        syncIfPossible();
        return 0;
    }

    public long extractDirect(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {

        if (getResource() == null || getResource().isBlank() || getAmount() <= 0)
        {
            amount = 0;
            return 0;
        }

        long extracted = Math.min(maxAmount, getAmount());

        if (extracted > 0 && resource.equals(getResource()))
        {
            this.updateSnapshots(transaction);
            amount -= extracted;
            return extracted;
        }
        syncIfPossible();
        return 0;
    }

    public void clear()
    {
        this.amount = 0;
        this.resource = FluidVariant.blank();
    }

    @Override
    public boolean supportsInsertion()
    {
        return mode.canInsert();
    }

    @Override
    public boolean supportsExtraction()
    {
        return mode.canExtract();
    }

    public enum Mode
    {
        INSERT_EXTRACT,
        INSERT_ONLY,
        EXTRACT_ONLY;

        boolean canInsert()
        {
            return this == INSERT_EXTRACT || this == INSERT_ONLY;
        }

        boolean canExtract()
        {
            return this == INSERT_EXTRACT || this == EXTRACT_ONLY;
        }
    }
}
