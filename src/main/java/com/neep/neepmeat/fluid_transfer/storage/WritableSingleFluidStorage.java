package com.neep.neepmeat.fluid_transfer.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("UnstableApiUsage")
public class WritableSingleFluidStorage extends SingleVariantStorage<FluidVariant>
{
    protected long capacity;

    public WritableSingleFluidStorage(long capacity)
    {
        this.capacity = capacity;
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

    public void writeNbt(NbtCompound nbt)
    {
        nbt.put("resource", getResource().toNbt());
        nbt.putLong("amount", amount);
    }

    public void readNbt(NbtCompound nbt)
    {
        this.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("resource"));
        this.amount = nbt.getLong("amount");
    }
}
