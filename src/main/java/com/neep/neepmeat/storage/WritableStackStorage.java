package com.neep.neepmeat.storage;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class WritableStackStorage extends SingleVariantStorage<ItemVariant>
{
    protected int capacity = 64;
    protected BlockEntity parent;

    public WritableStackStorage(@Nullable BlockEntity parent)
    {
        this.parent = parent;
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
//        System.out.println("writing " + getResource() + " =------------------------------------------");
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        this.amount = nbt.getLong("amount");
        this.variant = ItemVariant.fromNbt((NbtCompound) nbt.get("resource"));
//        System.out.println("reading " + " =------------ ------------------------------");
    }

    public void syncIfPossible()
    {
        if (parent != null)
        {
            parent.markDirty();

            if (parent instanceof BlockEntityClientSerializable becs)
            {
//                System.out.println("syncing is possible");
                becs.sync();
            }
        }
    }

    @Override
    protected ItemVariant getBlankVariant()
    {
        return ItemVariant.blank();
    }

    @Override
    protected long getCapacity(ItemVariant variant)
    {
        return capacity;
    }
}
