package com.neep.neepmeat.machine.grinder;

import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class GrinderStorage
{
    protected GrinderBlockEntity parent;

    public GrinderStorage(GrinderBlockEntity parent)
    {
        this.parent = parent;
        this.inputStorage = new WritableStackStorage(parent);
    }

    protected WritableStackStorage inputStorage;

    public Storage<ItemVariant> getItemStorage(Direction direction)
    {
        if (direction == Direction.UP || direction == null)
            return inputStorage;

        return null;
    }

    public void writeNbt(NbtCompound nbt)
    {
        inputStorage.writeNbt(nbt);
    }

    public void readNbt(NbtCompound nbt)
    {
        inputStorage.readNbt(nbt);
    }
}
