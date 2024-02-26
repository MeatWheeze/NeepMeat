package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.transport.block.fluid_transport.FluidGaugeBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FluidGaugeBlockEntity<T> extends SyncableBlockEntity
{
    private final LazyBlockApiCache<Storage<T>, Direction> cache;
    private FluidVariant foundVariant = FluidVariant.blank();
    private int comparatorOutput = 0;

    public FluidGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockApiLookup<Storage<T>, Direction> lookup)
    {
        super(type, pos, state);

        Direction facing = getCachedState().get(FluidGaugeBlock.FACING);
        BlockPos offsetPos = pos.offset(facing);

        cache = LazyBlockApiCache.of(lookup, offsetPos, this::getWorld, () -> facing);
    }

    public void serverTick()
    {
        if (getWorld().getTime() % 4 == 0)
        {
            int prevOutput = comparatorOutput;

            Storage<T> storage = cache.find();
            if (storage != null)
            {
                comparatorOutput = StorageUtil.calculateComparatorOutput(storage);
            }
            else
            {
                comparatorOutput = 0;
            }

            if (comparatorOutput != prevOutput)
            {
                world.setBlockState(pos, FluidGaugeBlock.getLevelState(getCachedState(), comparatorOutput));
                world.updateNeighbors(pos, getCachedState().getBlock());
            }
        }
    }

    public int getOutput()
    {
        return comparatorOutput;
    }
}
