package com.neep.meatlib.transfer;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class MultiFluidBuffer implements Storage<FluidVariant>
{
    long capacity;
    long totalAmount; // Cumulative contents of all slots
    long stagedTotalAmount; // Keeps track of the total amount within a single transaction
    BlockEntity parent;

    Predicate<FluidVariant> validTypes;
    ArrayList<Slot> slots = new ArrayList<>();

    public MultiFluidBuffer(@Nullable BlockEntity parent, long capacity, Predicate<FluidVariant> validTypes)
    {
        this.capacity = capacity;
        this.validTypes = validTypes;
        this.parent = parent;
        this.totalAmount = 0;

        slots.add(new Slot(FluidVariant.blank(), this));
    }

    @Override
    public String toString()
    {
        return slots.stream().map(slot -> slot.getResource().getFluid().toString() + " " + slot.getAmount() / FluidConstants.BUCKET).collect(Collectors.toList()).toString();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (!resource.isBlank())
        {
            Slot slot = getOrCreateSlot(resource);
            if (slot == null)
            {
                slot = new Slot(FluidVariant.blank(), this);
                slots.add(slot);
            }

            long insertedAmount = Math.min(maxAmount, capacity - totalAmount);
            if (insertedAmount > 0)
            {
                slot.insert(resource, insertedAmount, transaction);
            }
            syncIfPossible();
            return insertedAmount;
        }
        return 0;
    }

    protected void partInsertCallback(TransactionContext transaction, TransactionContext.Result result, long insertedAmount)
    {
        if (result.wasCommitted())
        {
            totalAmount = stagedTotalAmount;
        }
        else
        {
            stagedTotalAmount = totalAmount;
        }
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (!resource.isBlank())
        {
            Slot slot = getOrCreateSlot(resource);
            if (slot == null)
                return 0;

            long extractedAmount = Math.min(maxAmount, slot.getAmount());
            if (extractedAmount > 0)
            {
                slot.extract(resource, extractedAmount, transaction);
                if (slot.getAmount() <= 0)
                {
                    slots.remove(slot);
                }
            }
            syncIfPossible();
            return extractedAmount;
        }
        return 0;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        if (slots.isEmpty())
        {
//            return
        }
        return slots.stream().map(slot -> (StorageView<FluidVariant>) slot).iterator();
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putLong("total_amount", totalAmount);
        NbtList list = new NbtList();
        list.addAll(slots.stream()
                .filter(slot -> !slot.isResourceBlank())
                .map(slot ->
        {
            NbtCompound compound = new NbtCompound();
            slot.writeNbt(compound);
            return compound;
        }).collect(Collectors.toList()));
        nbt.put("parts", list);

        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        this.totalAmount = nbt.getLong("total_amount");
        NbtList list = (NbtList) nbt.get("parts");
        if (list == null)
            return;

        slots.clear();
        slots.addAll(list.stream().map(
                        compound -> Slot.fromNbt(this, (NbtCompound) compound)).collect(Collectors.toList()));
    }

    protected Slot getOrCreateSlot(FluidVariant variant)
    {
        for (Slot slot : slots)
        {
            if (slot.getResource().equals(variant))
                return slot;
        }
        return null;
    }

    public void syncIfPossible()
    {
        if (parent != null)
        {
            parent.markDirty();
        }
        if (parent instanceof BlockEntityClientSerializable serializable)
        {
            serializable.sync();
        }
    }

    public List<Slot> getSlots()
    {
        return slots;
    }

    public long getCapacity()
    {
        return capacity;
    }

    public long getTotalAmount()
    {
        return totalAmount;
    }

    public static class Slot extends SnapshotParticipant<ResourceAmount<FluidVariant>> implements SingleSlotStorage<FluidVariant>, StorageView<FluidVariant>
    {
        protected long amount;
        protected FluidVariant variant;
        protected MultiFluidBuffer parent;

        public Slot(FluidVariant variant, MultiFluidBuffer parent)
        {
            this.variant = variant;
            this.parent = parent;
        }

        @Override
        public String toString()
        {
            return variant.toString() + " " + amount;
        }

        public static Slot fromNbt(MultiFluidBuffer parent, NbtCompound nbt)
        {
            long amount = nbt.getLong("amount");
            FluidVariant variant = FluidVariant.fromNbt((NbtCompound) nbt.get("resource"));
            Slot slot = new Slot(variant, parent);
            slot.amount = amount;
            return slot;
        }

        public NbtCompound writeNbt(NbtCompound nbt)
        {
            nbt.putLong("amount", getAmount());
            nbt.put("resource", getResource().toNbt());
            return nbt;
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
            if ((insertedVariant.equals(variant) || variant.isBlank()))
            {
                this.variant = insertedVariant;
                long insertedAmount = Math.min(maxAmount, getCapacity() - maxAmount);
                if (insertedAmount > 0)
                {
                    updateSnapshots(transaction);
                    amount += insertedAmount;
                    parent.stagedTotalAmount += insertedAmount;
                    transaction.addCloseCallback((transaction1, result) -> parent.partInsertCallback(transaction1, result, insertedAmount));
                }
                return insertedAmount;
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction)
        {
            StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);
            updateSnapshots(transaction);
            long extractedAmount = Math.min(maxAmount, amount);
            amount -= extractedAmount;
            parent.stagedTotalAmount -= extractedAmount;

            if (amount <= 0)
                this.variant = FluidVariant.blank();

            transaction.addCloseCallback(((transaction1, result) -> parent.partInsertCallback(transaction1, result, extractedAmount)));
            return extractedAmount;
        }

        @Override
        public boolean isResourceBlank()
        {
            return variant.isBlank();
        }

        @Override
        public FluidVariant getResource()
        {
            return variant;
        }

        @Override
        public long getAmount()
        {
            return amount;
        }

        @Override
        public long getCapacity()
        {
            return parent.capacity - parent.stagedTotalAmount;
        }

        @Override
        protected ResourceAmount<FluidVariant> createSnapshot()
        {
            return new ResourceAmount<>(variant, amount);
        }

        @Override
        protected void readSnapshot(ResourceAmount<FluidVariant> snapshot)
        {
            this.variant = snapshot.resource();
            this.amount = snapshot.amount();
        }
    }

}