package com.neep.neepmeat.block;

import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface DirectionalFluidAcceptor extends FluidAcceptor
{
    boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction);

    @Override
    default AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (!connectInDirection(world, pos, state, direction))
        {
            return AcceptorModes.NONE;
        }
        return AcceptorModes.INSERT_EXTRACT;
    }
}
