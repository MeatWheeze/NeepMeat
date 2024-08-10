package com.neep.neepmeat.transport.api.pipe;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface DataCable
{
    default boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return true;
    }

    default boolean isConnected(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return true;
    }
}
