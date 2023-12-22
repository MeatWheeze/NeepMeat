package com.neep.neepmeat.api.storage;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * A slightly unnecessary, slightly deprecated vestige of where I had even less idea what I was doing.
 * I would add @Deprecated, but I don't want even more warnings.
 */
@SuppressWarnings("UnstableApiUsage")
public class WritableFluidBuffer extends WritableSingleFluidStorage implements FluidBuffer
{
    private final BlockEntity parent;

    public WritableFluidBuffer(@Nullable BlockEntity parent, long capacity)
    {
        super(capacity);
        this.parent = parent;
    }

    public NbtCompound toNbt(NbtCompound nbt)
    {
        nbt.putLong("amount", amount);
        nbt.put("resource", variant.toNbt());
        return nbt;
    }

    public NbtCompound readNbt(NbtCompound nbt)
    {
        this.amount = nbt.getLong("amount");
        this.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("resource"));
        return nbt;
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
    protected void onFinalCommit()
    {
        syncIfPossible();
    }

    public static boolean handleInteract(WritableSingleFluidStorage buffer, World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
        SoundEvent fill = buffer.variant.getFluid().getBucketFillSound().orElse(SoundEvents.ITEM_BUCKET_FILL);
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

    public void syncIfPossible()
    {
        if (parent != null)
        {
            parent.markDirty();
        }

        if (parent instanceof SyncableBlockEntity serializable)
        {
            serializable.sync();
        }
    }
}
