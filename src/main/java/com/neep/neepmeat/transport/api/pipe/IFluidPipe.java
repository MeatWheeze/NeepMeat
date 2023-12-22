package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.css.CSSStyleSheet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    static List<Direction> getConnections(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        if (world.getBlockState(pos).getBlock() instanceof IFluidPipe pipe)
        {
            return pipe.getConnections(state, d -> true);
        }
        return Collections.emptyList();
    }

    default void updateNetwork(ServerWorld world, BlockPos pos, BlockState state, PipeNetworkImpl1.UpdateReason reason)
    {
        try
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
                BlockPos.Mutable mutable = pos.mutableCopy();
                for (Direction direction : this.getConnections(state, d -> true))
                {
                    mutable.set(pos, direction);
                    net = PipeNetwork.LOOKUP.find(world, mutable, null);
                    if (net != null)
                    {
                        net.update(mutable.toImmutable(), null, reason);
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
                    mergeNetworks.get(0).merge(mutable.toImmutable(), network);
                    merged = true;
                }
                if (merged) return;
            }

            {
                // If there are no adjacent networks, try to create one here.
                PipeNetwork.tryCreateNetwork(world, pos);
            }
        }
        catch (Exception e)
        {
            ExceptionUtils.getRootCause(e).printStackTrace();
            throw e;
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

    default boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return true;
    }

    default AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return AcceptorModes.INSERT_EXTRACT;
    }

    default PipeVertex getPipeVertex(ServerWorld world, BlockPos pos, BlockState state)
    {
        return new SimplePipeVertex();
    }
}
