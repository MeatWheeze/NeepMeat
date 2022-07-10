package com.neep.meatlib.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("UnstableApiUsage")
public class WritableSingleFluidStorage extends SingleVariantStorage<ItemVariant>
{
    protected long capacity = 64;

    public WritableSingleFluidStorage()
    {
    }

    @Override
    protected void onFinalCommit()
    {
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

    @Override
    protected ItemVariant getBlankVariant()
    {
        return ItemVariant.blank();
    }

    @Override
    public long getCapacity(ItemVariant variant)
    {
        return capacity;
    }

    public void setCapacity(long capacity)
    {
        this.capacity = capacity;
    }
}
