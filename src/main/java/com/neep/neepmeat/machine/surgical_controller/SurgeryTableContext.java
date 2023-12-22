package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.recipe.surgery.TableComponent;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A mutable view of the surgery table's constituent storages
 */
public class SurgeryTableContext implements NbtSerialisable
{
    private static Direction CONTEXT = Direction.DOWN;

    private World world;
    private List<BlockPos> posList;

    @Nullable
    // TODO: This is a problem
    public TableComponent<? extends TransferVariant<?>> getStructure(int i)
    {
        return TableComponent.STRUCTURE_LOOKUP.find(world, posList.get(i), null);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {

    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    public interface Part<T>
    {

    }
}
