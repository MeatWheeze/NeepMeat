package com.neep.neepmeat.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
}
