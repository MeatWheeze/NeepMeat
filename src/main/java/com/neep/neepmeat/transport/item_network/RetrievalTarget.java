package com.neep.neepmeat.transport.item_network;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public record RetrievalTarget<T extends TransferVariant<?>>(BlockApiCache<Storage<T>, Direction> cache, Direction accessFace)
{
    public static <T extends TransferVariant<?>> RetrievalTarget<T> of(BlockApiCache<Storage<T>, Direction> cache, Direction accessFace)
    {
        return new RetrievalTarget<>(cache, accessFace);
    }

    public static <T extends TransferVariant<?>> RetrievalTarget<T> of(BlockApiLookup<Storage<T>, Direction> lookup, ServerWorld world, BlockPos pos, Direction accessFace)
    {
        return new RetrievalTarget<>(BlockApiCache.create(lookup, world, pos), accessFace);
    }

    public BlockPos getPos()
    {
        return cache.getPos();
    }

    @Nullable public Storage<T> find()
    {
        return cache.find(accessFace);
    }
}
