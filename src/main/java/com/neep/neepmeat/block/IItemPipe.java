package com.neep.neepmeat.block;

import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface IItemPipe
{
    static boolean isConnectedIn(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        else if (state.getBlock() instanceof IItemPipe acceptor)
        {
            return acceptor.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    static Iterable<Direction> getConnections(BlockState state)
    {
        // Streams are good, aren't they?
        return Arrays.stream(Direction.values()).filter(dir -> state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(dir)).isConnected()).collect(Collectors.toList());
    }

    default boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return true;
    }

    default AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return AcceptorModes.INSERT_EXTRACT;
    }
}
