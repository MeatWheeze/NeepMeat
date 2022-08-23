package com.neep.meatlib.transfer;

import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MultiItemBuffer extends CombinedStorage<ItemVariant, WritableStackStorage>
{
    public MultiItemBuffer(List<WritableStackStorage> parts)
    {
        super(parts);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        for (int i = 0; i < parts.size(); ++i)
        {
            NbtCompound nbt2 = new NbtCompound();
            nbt2 = parts.get(i).writeNbt(nbt2);
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
