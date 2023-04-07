package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface IDirectionalFluidAcceptor extends IFluidPipe
{
    boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction);

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
