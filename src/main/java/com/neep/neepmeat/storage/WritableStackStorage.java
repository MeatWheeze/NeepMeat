package com.neep.neepmeat.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

@SuppressWarnings("UnstableApiUsage")
public class WritableStackStorage extends SingleVariantStorage<ItemVariant> implements StorageView<ItemVariant>
{
    protected int capacity = 64;
    protected BlockEntity parent;

    public WritableStackStorage(@Nullable BlockEntity parent)
    {
        this.parent = parent;
    }

    public WritableStackStorage(@Nullable BlockEntity parent, int capacity)
    {
        this.parent = parent;
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
        if (parent != null)
        {
            parent.markDirty();
            parent.getWorld().updateListeners(parent.getPos(), parent.getCachedState(), parent.getCachedState(), Block.NOTIFY_LISTENERS);
        }
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
            return variant.getItem().getMaxCount();
        }
        return capacity;
    }
}
