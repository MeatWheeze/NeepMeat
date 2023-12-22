package com.neep.neepmeat.machine.grinder;

import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class GrinderStorage extends SimpleInventory
{
    protected GrinderBlockEntity parent;
    protected WritableStackStorage inputStorage;
    protected WritableStackStorage outputStorage;

    public GrinderStorage(GrinderBlockEntity parent)
    {
        this.parent = parent;
        this.inputStorage = new WritableStackStorage(parent);
        this.outputStorage = new WritableStackStorage(parent, 1);
    }

    public Storage<ItemVariant> getItemStorage(Direction direction)
    {
        if (direction == Direction.UP || direction == null)
        {
            return inputStorage;
        }
        else if (direction == parent.getCachedState().get(GrinderBlock.FACING))
        {
            return outputStorage;
        }
        return null;
    }

    public WritableStackStorage getInputStorage()
    {
        return inputStorage;
    }

    public void writeNbt(NbtCompound nbt)
    {
        inputStorage.writeNbt(nbt);
    }

    public void readNbt(NbtCompound nbt)
    {
        inputStorage.readNbt(nbt);
    }

    public Storage<ItemVariant> getOutputStorage()
    {
        return outputStorage;
    }

    public void dropItems(World world, BlockPos pos)
    {
        ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inputStorage.getAsStack());
        ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, outputStorage.getAsStack());
    }
}
