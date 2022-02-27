package com.neep.neepmeat.fluid_util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;

import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class TypedFluidBuffer extends FluidBuffer implements Storage<FluidVariant>, SingleSlotStorage<FluidVariant>
{
    protected Predicate<FluidVariant> validTypes;

    public TypedFluidBuffer(BlockEntity parent, long capacity, Predicate<FluidVariant> validTypes)
    {
        super(parent, capacity);
        this.validTypes = validTypes;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
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

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
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
}
