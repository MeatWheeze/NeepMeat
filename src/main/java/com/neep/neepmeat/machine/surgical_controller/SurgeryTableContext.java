package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.recipe.surgery.TableComponent;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A mutable view of the surgery table's constituent storages
 */
public class SurgeryTableContext implements NbtSerialisable
{
    private List<BlockApiCache<TableComponent<?>, Void>> caches = new ArrayList<>(9);

    private World world;
    private List<BlockPos> posList;

    @Nullable
    public TableComponent<? extends TransferVariant<?>> getStructure(int i)
    {
        // TODO: This is a problem
//        return (TableComponent<? extends TransferVariant<?>>) TableComponent.STRUCTURE_LOOKUP.find(world, posList.get(i), null);
        return (TableComponent<? extends TransferVariant<?>>) caches.get(i).find(null);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {

    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    public void add(ServerWorld world, BlockPos pos)
    {
        caches.add(BlockApiCache.create(TableComponent.STRUCTURE_LOOKUP, world, pos));
    }

    public void clear()
    {
        caches.clear();
    }

    public interface Part<T>
    {

    }
}
