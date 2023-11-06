package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.Sets;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
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
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        if (!newState.isOf(state.getBlock())
                && world instanceof ServerWorld serverWorld
                && serverWorld.getBlockEntity(pos) instanceof FluidPipeBlockEntity<?> be)
        {
            be.markReplaced();
        }
    }

    default boolean createStorageNodes(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            boolean changed = false;
            List<Direction> connections = getConnections(state, dir -> true);
            for (Direction direction : Direction.values())
            {
//                if (state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
                if (connections.contains(direction))
                {
                    if (FluidNodeManager.getInstance(world).updatePosition(world, new NodePos(pos, direction)))
                        changed = true;
                }
                else
                {
                    FluidNodeManager.getInstance(world).removeNode(world, new NodePos(pos, direction));
                    changed = true;
                }
            }
            return changed;
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
        else if (state.getBlock() instanceof AbstractAxialFluidPipe)
        {
            Direction facing = state.get(AbstractAxialFluidPipe.FACING);
            return List.of(facing, facing.getOpposite()).stream().filter(forbidden).collect(Collectors.toList());
        }
        else
        {
            return List.of();
        }
    }

    static List<Direction> getConnections(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        if (world.getBlockState(pos).getBlock() instanceof FluidPipe pipe)
        {
            return pipe.getConnections(state, d -> true);
        }
        return Collections.emptyList();
    }

    default void updateNetwork(ServerWorld world, BlockPos pos, BlockState state, PipeNetwork.UpdateReason reason)
    {
        PipeNetwork net = PipeNetwork.LOOKUP.find(world, pos, null);
        if (net != null)
        {
            net.update(pos, null, reason);
            return;
        }
        else if (reason.isRemoved())
        {

            // Look for adjacent networks and add this pipe to the first one.
            Set<PipeNetwork> updatedNetworks = Sets.newHashSet();
            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Direction direction : this.getConnections(state, d -> true))
            {
                mutable.set(pos, direction);
                net = PipeNetwork.LOOKUP.find(world, mutable, null);
                if (net != null)
                {
                    if (!updatedNetworks.contains(net))
                    {
                        net.update(mutable.toImmutable(), null, reason);
                        updatedNetworks.add(net);
                    }
                }
                else
                {
                    PipeNetwork.tryCreateNetwork(world, mutable.toImmutable());
                }
            }
            return;
        }
        else if (reason.isNewPart())
        {
            List<PipeNetwork> mergeNetworks = Lists.newArrayList();
            BlockPos.Mutable mutable = pos.mutableCopy();
            boolean merged = false;
            for (Direction direction : this.getConnections(state, d -> true))
            {
                mutable.set(pos, direction);
                net = PipeNetwork.LOOKUP.find(world, mutable, null);
                if (net != null)
                {
                    mergeNetworks.add(net);
                }
            }
            for (PipeNetwork network : mergeNetworks)
            {
                mergeNetworks.get(0).merge(pos, network);
                merged = true;
            }
            if (merged) return;
        }

        {
            // If there are no adjacent networks, try to create one here.
            PipeNetwork.tryCreateNetwork(world, pos);
        }
    }

    default void addPipe(ServerWorld world, BlockState state, BlockPos pos)
    {

    }

    default void removePipe(ServerWorld world, BlockState state, BlockPos pos)
    {
        FluidNodeManager.removeStorageNodes(world, pos);
//        world.removeBlockEntity(pos); // Just in case
//        for (Direction direction : getConnections(state, dir -> true))
//        {
//            updateNetwork(world, pos.offset(direction), PipeNetwork.UpdateReason.PIPE_REMOVED);
//        }
        updateNetwork(world, pos, state, PipeNetwork.UpdateReason.PIPE_REMOVED);
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
}
