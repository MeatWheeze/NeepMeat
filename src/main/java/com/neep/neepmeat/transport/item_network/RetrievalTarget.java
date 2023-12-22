package com.neep.neepmeat.transport.item_network;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class RetrievalTarget<T extends TransferVariant<?>>
{
    protected BlockApiCache<Storage<T>, Direction> cache;
    protected Direction accessFace;

    public RetrievalTarget(BlockApiCache<Storage<T>, Direction> cache, Direction accessFace)
    {
        this.cache = cache;
        this.accessFace = accessFace;
    }

    public BlockApiCache<Storage<T>, Direction> getCache()
    {
        return cache;
    }

    public Direction getFace()
    {
        return accessFace;
    }

    public BlockPos getPos()
    {
        return cache.getPos();
    }

    public Storage<T> find()
    {
        return cache.find(accessFace);
    }
}
