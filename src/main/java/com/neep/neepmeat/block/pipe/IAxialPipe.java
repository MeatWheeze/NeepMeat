package com.neep.neepmeat.block.pipe;

import com.neep.neepmeat.block.AbstractPipeBlock;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IAxialPipe extends IFluidPipe
{
    static boolean isConnectedIn(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        else if (state.getBlock() instanceof IAxialPipe acceptor)
        {
            return acceptor.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    @Override
    default boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof FacingBlock)
        {
            Direction facing = state.get(FacingBlock.FACING);
            return direction == facing || direction == facing.getOpposite();
        }
        return true;
    }

    default AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return AcceptorModes.INSERT_EXTRACT;
    }
}
