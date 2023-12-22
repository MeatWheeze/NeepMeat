package com.neep.neepmeat.block.pipe;

import com.neep.neepmeat.block.AbstractPipeBlock;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.PipeConnectionType;
import com.neep.neepmeat.fluid_transfer.PipeNetwork;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IFluidPipe
{

    static boolean isConnectedIn(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        else if (state.getBlock() instanceof IFluidPipe acceptor)
        {
            return acceptor.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    default boolean createStorageNodes(World world, BlockPos pos, BlockState state)
    {
        System.out.println("eeeeeeeeeeee");
        if (!world.isClient)
        {
            boolean newNode = false;
            List<Direction> connections = getConnections(state, dir -> true);
            System.out.println("oooooooo " + connections);
            for (Direction direction : Direction.values())
            {
//                if (state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
                if (connections.contains(direction))
                {
                    if (FluidNetwork.getInstance(world).updatePosition(world, new NodePos(pos, direction)))
                        newNode = true;
                }
                else
                {
                    FluidNetwork.getInstance(world).removeNode(world, new NodePos(pos, direction));
                }
            }
            return newNode;
        }
        return false;
    }

    default List<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            // Streams are good, aren't they?
            return Arrays.stream(Direction.values())
                    .filter(dir -> state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(dir)).isConnected()).filter(forbidden)
                    .collect(Collectors.toList());
        }
        else if (state.getBlock() instanceof AbstractAxialPipe)
        {
            Direction facing = state.get(AbstractAxialPipe.FACING);
            return List.of(facing, facing.getOpposite()).stream().filter(forbidden).collect(Collectors.toList());
        }
        else
        {
            return List.of();
        }
    }

    default void updateNetwork(ServerWorld world, BlockPos pos, PipeNetwork.UpdateReason reason)
    {
        Optional<PipeNetwork> net = PipeNetwork.tryCreateNetwork(world, pos, Direction.NORTH);
    }

    default void addPipe(ServerWorld world, BlockState state, BlockPos pos)
    {

    }

    default void removePipe(ServerWorld world, BlockState state, BlockPos pos)
    {
        FluidNetwork.removeStorageNodes(world, pos);
        world.removeBlockEntity(pos); // Just in case
        for (Direction direction : getConnections(state, dir -> true))
        {
            updateNetwork(world, pos.offset(direction), PipeNetwork.UpdateReason.PIPE_BROKEN);
        }
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
