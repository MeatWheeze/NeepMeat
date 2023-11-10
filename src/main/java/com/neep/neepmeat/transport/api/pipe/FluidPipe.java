package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.Iterables;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.node.BlockPipeVertex;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface FluidPipe
{
    static boolean isConnectedIn(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        else if (state.getBlock() instanceof FluidPipe acceptor)
        {
            return acceptor.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    static Optional<FluidPipe> findFluidPipe(World world, BlockPos pos, BlockState state)
    {
        if (state.getBlock() instanceof FluidPipe pipe) return Optional.of(pipe);
        return Optional.empty();
    }

    // Call this first
    static void onStateReplaced(World world, BlockPos pos, BlockState state, BlockState newState)
    {
        if (world.getBlockEntity(pos) instanceof FluidPipeBlockEntity<?> be)
        {
            if (!newState.isOf(state.getBlock()) && world instanceof ServerWorld serverWorld)
            {
                be.markReplaced();
            }
        }
    }

    default boolean createStorageNodes(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            boolean changed = false;
            for (Direction direction : Direction.values())
            {
                if (isConnectedIn(world, pos, state, direction))
                {
                    if (FluidNodeManager.getInstance(world).updatePosition(world, new NodePos(pos, direction)))
                        changed = true;
                }
                else
                {
                    changed |= FluidNodeManager.getInstance(world).removeNode(new NodePos(pos, direction));
                }
            }
            return changed;
        }
        return false;
    }

    default Iterable<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return () -> Arrays.stream(Direction.values())
                    .filter(dir -> state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(dir)).isConnected())
                    .filter(forbidden)
                    .iterator();
        }
        else if (state.getBlock() instanceof AbstractAxialFluidPipe)
        {
            Direction facing = state.get(AbstractAxialFluidPipe.FACING);
            return () -> Stream.of(facing, facing.getOpposite())
                    .filter(forbidden)
                    .iterator();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    default void removePipe(ServerWorld world, BlockState state, BlockPos pos)
    {
        FluidNodeManager.removeStorageNodes(world, pos);
//        updateNetwork(world, pos, state, PipeNetwork.UpdateReason.PIPE_REMOVED);
    }

    default boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return true;
    }

    default AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return AcceptorModes.INSERT_EXTRACT;
    }

    default PipeVertex getPipeVertex(ServerWorld world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof FluidPipeBlockEntity be)
        {
            // TODO: remove this cast
            ((BlockPipeVertex) be.getPipeVertex()).updateNodes((ServerWorld) world, pos.toImmutable(), state);
            return be.getPipeVertex();
        }
        return null;
    }

    default int countConnections(BlockState blockState)
    {
        return Iterables.size(getConnections(blockState, d -> true));
    }
}
