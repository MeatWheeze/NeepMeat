package com.neep.neepmeat.transport.fluid_network;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface ISpecialPipe
{
    FilterFunction getFlowFunction(World world, Direction bias, BlockPos pos, BlockState vertex);

    boolean canTransferFluid(Direction bias, BlockState vertex);

}
