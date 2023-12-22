package com.neep.neepmeat.recipe.surgery;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

public abstract class TableComponentImpl<T> implements TableComponent<T>
{
    public static final Direction CTX = Direction.DOWN;

    final BlockApiLookup<Storage<T>, Direction> lookup;
    protected BlockApiCache<Storage<T>, Direction> cache;

    protected TableComponentImpl(BlockApiLookup<Storage<T>, Direction> lookup)
    {
        this.lookup = lookup;
    }

    @Override
    public BlockApiLookup<Storage<T>, Direction> getSidedLookup()
    {
        return lookup;
    }

    @Override
    public Storage<T> getStorage()
    {
        return cache.find(CTX);
    }
}
