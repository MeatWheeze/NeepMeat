package com.neep.neepmeat.fluid_util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import javax.xml.xpath.XPathEvaluationResult;
import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class FluidBuffer extends SnapshotParticipant<ResourceAmount<FluidVariant>> implements Storage<FluidVariant>, SingleSlotStorage<FluidVariant>
{

    private long capacity;
    private FluidVariant resource = FluidVariant.blank();
    private long amount = 0;
    private BlockEntity parent;

    public FluidBuffer(BlockEntity parent, long capacity)
    {
        this.capacity = capacity;
        this.parent = parent;
    }

    public NbtCompound writeNBT(NbtCompound compound)
    {
//        NbtCompound compound1 = new NbtCompound();
        compound.putLong("amount", amount);

//        compound.put("buffer", compound1);
        return compound;
    }

    public void readNBT(NbtCompound compound)
    {
        this.amount = compound.getLong("amount");
    }

    @Override
    public boolean supportsExtraction()
    {
        return true;
    }

    @Override
    public boolean supportsInsertion()
    {
        return true;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
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
        return 0;
    }

    @Override
    public boolean isResourceBlank()
    {
        return resource.equals(FluidVariant.blank());
    }

    @Override
    public FluidVariant getResource()
    {
        return resource;
    }

    @Override
    public long getAmount()
    {
        return amount;
    }

    @Override
    public long getCapacity()
    {
        return capacity;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        return SingleViewIterator.create(this, transaction);
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot()
    {
        return new ResourceAmount<>(resource, amount);
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot)
    {
        resource = snapshot.resource();
        amount = snapshot.amount();
    }
}