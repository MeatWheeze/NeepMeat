package com.neep.neepmeat.api.storage;

import com.neep.neepmeat.fluid.MixableFluid;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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

    public static boolean handleInteract(WritableSingleFluidStorage buffer, World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
        SoundEvent fill = buffer.variant.getFluid().getBucketFillSound().orElse(SoundEvents.ITEM_BUCKET_FILL);

        if (world.isClient())
            return true;

        if (storage != null)
        {
            if (player.isCreative())
            {

                try (Transaction transaction = Transaction.openOuter())
                {
                    StorageView<FluidVariant> view = storage.iterator().next();
                    if (!view.isResourceBlank())
                    {
                        world.playSound(null, player.getBlockPos(), fill, SoundCategory.BLOCKS, 1f, 1.5f);
                        buffer.insert(view.getResource(), view.getAmount(), transaction);
                        transaction.commit();
                        return true;
                    }
                    transaction.abort();
                }
            }

            Transaction inner;
            try (Transaction transaction = Transaction.openOuter())
            {
                inner = transaction.openNested();
                if (StorageUtil.move(storage, buffer, variant -> true, Long.MAX_VALUE, inner) > 0)
                {

                    world.playSound(null, player.getBlockPos(), fill, SoundCategory.BLOCKS, 1f, 1.5f);
                    inner.commit();
                    transaction.commit();
                    return true;
                }
                inner.abort();

                inner = transaction.openNested();
                if (StorageUtil.move(buffer, storage, variant -> true, Long.MAX_VALUE, inner) > 0)
                {
                    world.playSound(null, player.getBlockPos(), fill, SoundCategory.BLOCKS, 1f, 1.5f);
                    inner.commit();
                    transaction.commit();
                    return true;
                }
                inner.abort();
                transaction.abort();
            }
        }
        return false;
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
