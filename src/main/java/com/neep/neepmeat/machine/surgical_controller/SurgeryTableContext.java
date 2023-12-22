package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.recipe.surgery.TableComponent;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A mutable view of the surgery table's constituent storages
 */
@SuppressWarnings("UnstableApiUsage")
public class SurgeryTableContext implements NbtSerialisable
{
    private final List<BlockApiCache<TableComponent<?>, Void>> caches = new ArrayList<>(9);
    private final List<BlockPos> posList = new ArrayList<>(9);

    public boolean isAssembled;

    WritableStackStorage storage = new WritableStackStorage(() -> {}, Integer.MAX_VALUE)
    {
        @Override
        public boolean supportsInsertion()
        {
            return false;
        }
    };

    @Nullable
    public TableComponent<TransferVariant<?>> getStructure(int i)
    {
        // TODO: This is a problem
//        return (TableComponent<? extends TransferVariant<?>>) TableComponent.STRUCTURE_LOOKUP.find(world, posList.get(i), null);
        return (TableComponent<TransferVariant<?>>) caches.get(i).find(null);
    }

    public int getWidth()
    {
        return 3;
    }

    public int getHeight()
    {
        return 3;
    }

    public int getSize()
    {
        return posList.size();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        storage.writeNbt(nbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        storage.readNbt(nbt);
    }

    public void add(ServerWorld world, BlockPos pos)
    {
        posList.add(pos.toImmutable());
        caches.add(BlockApiCache.create(TableComponent.STRUCTURE_LOOKUP, world, pos));
    }

    public BlockPos getPos(int i)
    {
        return posList.get(i);
    }

    public void clear()
    {
        caches.clear();
        posList.clear();
    }

    public Storage<ItemVariant> getStorage()
    {
        return storage;
    }
}
