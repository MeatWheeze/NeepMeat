package com.neep.neepmeat.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class WritableStackStorage extends SingleVariantStorage<ItemVariant> implements StorageView<ItemVariant>
{
    protected int capacity;
    protected Runnable callback;

    public WritableStackStorage(@Nullable Runnable parent)
    {
        this(parent, 64);
    }

    public WritableStackStorage(@Nullable Runnable parent, int capacity)
    {
        this.callback = parent;
        this.capacity = capacity;
    }

    @Override
    protected void onFinalCommit()
    {
        syncIfPossible();
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putLong("amount", getAmount());
        nbt.put("resource", getResource().toNbt());
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        this.amount = nbt.getLong("amount");
        this.variant = ItemVariant.fromNbt((NbtCompound) nbt.get("resource"));
    }

    public void syncIfPossible()
    {
        if (callback != null)
            callback.run();
    }

    public void setStack(ItemStack stack)
    {
        this.variant = ItemVariant.of(stack);
        this.amount = stack.getCount();
        syncIfPossible();
    }

    public ItemStack getAsStack()
    {
        return variant.toStack((int) getAmount());
    }

    public boolean isEmpty()
    {
        return this.amount == 0 || this.variant.isBlank();
    }

    @Override
    protected ItemVariant getBlankVariant()
    {
        return ItemVariant.blank();
    }

    @Override
    protected long getCapacity(ItemVariant variant)
    {
        if (!variant.isBlank())
        {
            return Math.min(variant.getItem().getMaxCount(), capacity);
        }
        return capacity;
    }

    public static boolean handleInteract(PlayerEntity player, Hand hand, SingleVariantStorage<ItemVariant> storage)
    {
        ItemStack stack = player.getStackInHand(hand);
//            System.out.println(getResource() +", " + isResourceBlank() + ", stack: " + stack.isEmpty());
        if ((stack.isEmpty() || !storage.getResource().matches(stack)) && !storage.isResourceBlank())
        {
            Transaction transaction = Transaction.openOuter();
            {
                ItemVariant resource = storage.getResource();
                long extracted = storage.extract(resource, Long.MAX_VALUE, transaction);
                player.giveItemStack(resource.toStack((int) extracted));
                transaction.commit();
                return true;
            }
        }
        else if (storage.isResourceBlank() && !stack.isEmpty())
        {
            Transaction transaction = Transaction.openOuter();
            {
                long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);
                transaction.commit();
                return true;
            }
        }
        return false;
    }
}
