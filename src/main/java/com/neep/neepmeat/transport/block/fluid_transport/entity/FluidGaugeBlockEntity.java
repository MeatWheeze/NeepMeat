package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.transport.block.fluid_transport.FluidGaugeBlock;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FluidGaugeBlockEntity extends SyncableBlockEntity
{
    private final LazyBlockApiCache<Storage<FluidVariant>, Direction> cache;
    private FluidVariant foundVariant = FluidVariant.blank();
    private int comparatorOutput = 0;

    public FluidGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        Direction facing = getCachedState().get(FluidGaugeBlock.FACING);
        BlockPos offsetPos = pos.offset(facing);

        cache = LazyBlockApiCache.of(FluidStorage.SIDED, offsetPos, this::getWorld, () -> facing);
    }

    public void serverTick()
    {
        if (getWorld().getTime() % 4 == 0)
        {
            int prevOutput = comparatorOutput;

            Storage<FluidVariant> storage = cache.find();
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
//                world.updateComparators(pos, getCachedState().getBlock());
//                world.updateNeighbors(pos, getCachedState().getBlock());
                world.setBlockState(pos, FluidGaugeBlock.getLevelState(getCachedState(), comparatorOutput));
            }
        }
    }

    public int getOutput()
    {
        return comparatorOutput;
    }
}
