package com.neep.neepmeat.transport.block.fluid_transport;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IVariableFlowBlock extends IDirectionalFluidAcceptor
{
    float getFlow(World world, BlockPos pos, BlockState state, Direction direction);
}
