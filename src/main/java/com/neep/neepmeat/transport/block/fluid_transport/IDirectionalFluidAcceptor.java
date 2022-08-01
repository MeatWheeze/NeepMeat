package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.neepmeat.api.block.pipe.IFluidPipe;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IDirectionalFluidAcceptor extends IFluidPipe
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
