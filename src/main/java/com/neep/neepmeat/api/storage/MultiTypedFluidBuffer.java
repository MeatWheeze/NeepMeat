package com.neep.neepmeat.api.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MultiTypedFluidBuffer extends CombinedStorage<FluidVariant, TypedFluidBuffer>
{
    protected BlockEntity parent;
    List<TypedFluidBuffer> buffers;

    public MultiTypedFluidBuffer(BlockEntity parent, List<TypedFluidBuffer> buffers)
    {
        super(buffers);
        this.buffers = buffers;
        this.parent = parent;
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        for (int i = 0; i < buffers.size(); ++i)
        {
            NbtCompound nbt2 = new NbtCompound();
            buffers.get(i).writeNbt(nbt2);
            nbt.put("buffer_" + i, nbt2);
        }
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        for (int i = 0; i < buffers.size(); ++i)
        {
            NbtCompound nbt2 = nbt.getCompound("buffer_" + i);
            buffers.get(i).readNbt(nbt2);
        }
    }
}
