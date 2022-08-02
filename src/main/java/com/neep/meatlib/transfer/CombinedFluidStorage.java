package com.neep.meatlib.transfer;

import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

/**
 * I had no idea what I was doing when I made this
 */
@SuppressWarnings("UnstableApiUsage")
public class CombinedFluidStorage extends CombinedStorage<FluidVariant, WritableFluidBuffer>
{
    public CombinedFluidStorage(List<WritableFluidBuffer> parts)
    {
        super(parts);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        for (int i = 0; i < parts.size(); ++i)
        {
            NbtCompound nbt2 = new NbtCompound();
            parts.get(i).writeNbt(nbt2);
            nbt.put("part_" + i, nbt2);
        }
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        for (int i = 0; i < parts.size(); ++i)
        {
            NbtCompound nbt2 = nbt.getCompound("part_" + i);
            parts.get(i).readNbt(nbt2);
        }
    }
}
