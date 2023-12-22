package com.neep.meatlib.transfer;

import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class MultiFluidBuffer extends SnapshotParticipant<Map<FluidVariant, Long>> implements Storage<FluidVariant>
{
    long capacity;
    protected final Runnable finalCallback;

    Predicate<FluidVariant> validTypes;
    protected final Object2LongOpenHashMap<FluidVariant> map = new Object2LongOpenHashMap<>();

    public MultiFluidBuffer(long capacity, Predicate<FluidVariant> validTypes, Runnable finalCallback)
    {
        this.capacity = capacity;
        this.validTypes = validTypes;
        this.finalCallback = finalCallback;

//        slots.add(new Slot(FluidVariant.blank(), this));
    }

    @Override
    public String toString()
    {
        return map.toString();
//        return slots.stream().map(slot -> slot.getResource().getFluid().toString() + " " + slot.getAmount() / FluidConstants.BUCKET).collect(Collectors.toList()).toString();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        long insertedAmount = Math.min(maxAmount, getSpace());
        if (insertedAmount > 0)
        {
            updateSnapshots(transaction);
            map.compute(resource, (r, a) -> a != null ? a + insertedAmount : insertedAmount);
        }
        return insertedAmount;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        long amount = map.getLong(resource);
        long extracted = (Math.min(maxAmount, amount));
        if (extracted > 0)
        {
            updateSnapshots(transaction);
            map.addTo(resource, -extracted);
            if ((amount - extracted) <= 0) map.remove(resource);
        }
        return extracted;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        return map.object2LongEntrySet().stream().map(this::ofEntry).iterator();
    }

    // This is probably highly inefficient and not a proper solution. Help.
    protected StorageView<FluidVariant> ofEntry(Map.Entry<FluidVariant, Long> entry)
    {
        SingleVariantStorage<FluidVariant> view = new SingleVariantStorage<>()
        {
            @Override
            protected FluidVariant getBlankVariant()
            {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant)
            {
                return MultiFluidBuffer.this.getCapacity();
            }

            @Override
            public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
            {
                return MultiFluidBuffer.this.insert(insertedVariant, maxAmount, transaction);
            }

            @Override
            public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction)
            {
                return MultiFluidBuffer.this.extract(extractedVariant, maxAmount, transaction);
            }
        };

        view.variant = entry.getKey();
        view.amount = entry.getValue();

        return view;
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        map.forEach((r, a) ->
        {
            NbtCompound comp = r.toNbt();
            comp.putLong("amount", a);
            list.add(comp);
        });
        nbt.put("parts", list);

        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        NbtList list = nbt.getList("parts", 10);

        if (list == null)
            return;

        map.clear();
        list.forEach(c -> map.put(FluidVariant.fromNbt((NbtCompound) c), ((NbtCompound) c).getLong("amount")));
    }

    public boolean handleInteract(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
//        SoundEvent fill = s.getFluid().getBucketFillSound().orElse(SoundEvents.ITEM_BUCKET_FILL);
        if (storage != null)
        {
            if (StorageUtil.move(storage, this, variant -> true, Long.MAX_VALUE, null) > 0)
            {
//                world.playSound(null, player.getBlockPos(), fill, SoundCategory.BLOCKS, 1f, 1.5f);
                return true;
            }

            if (StorageUtil.move(this, storage, variant -> true, Long.MAX_VALUE, null) > 0)
            {
//                world.playSound(null, player.getBlockPos(), fill, SoundCategory.BLOCKS, 1f, 1.5f);
                return true;
            }
        }
        return false;
    }

    public long getCapacity()
    {
        return capacity;
    }

    public long getTotalAmount()
    {
        // Not sure if there is a better way
        AtomicLong l = new AtomicLong();
        Object2LongMaps.fastForEach(map, (entry) -> l.addAndGet(entry.getLongValue()));
        return l.get();
    }

    public long getSpace()
    {
        return getCapacity() - getTotalAmount();
    }

    public Object2LongMap.FastEntrySet<FluidVariant> getSlots()
    {
        return map.object2LongEntrySet();
    }

    @Override
    protected Map<FluidVariant, Long> createSnapshot()
    {
        return map.clone();
    }

    @Override
    protected void readSnapshot(Map<FluidVariant, Long> snapshot)
    {
        map.clear();
        map.putAll(snapshot);
    }

    @Override
    protected void onFinalCommit()
    {
        super.onFinalCommit();
        finalCallback.run();
    }
}