package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
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

    static FluidPipe findFluidPipe(World world, BlockPos pos, BlockState state)
    {
        if (state.getBlock() instanceof FluidPipe pipe)
            return pipe;

        return null;
    }

    // Call this first
    static void onStateReplaced(World world, BlockPos pos, BlockState state, BlockState newState, FluidPipe fluidPipe)
    {
        if (!world.isClient())
        {
            if (world.getBlockEntity(pos) instanceof FluidPipeBlockEntity<?> be)
            {
                if (!newState.isOf(state.getBlock()))
                {
                    be.markReplaced();

                    fluidPipe.propagateRemoval((ServerWorld) world, pos, state);
                }
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

    default void propagateRemoval(ServerWorld world, BlockPos pos, BlockState oldState)
    {
        var toNotify = getNearestVertices(world, pos, oldState);
        for (var pair : toNotify)
        {
            pair.first().setAdjVertex(pair.second().getOpposite().ordinal(), null);
        }
    }

    default List<Pair<PipeVertex, Direction>> getNearestVertices(ServerWorld world, BlockPos start, BlockState oldState)
    {
        List<Pair<PipeVertex, Direction>> vertices = Lists.newArrayList();

        Set<BlockPos> visited = Sets.newHashSet();
        Deque<BlockPos> stack = Queues.newArrayDeque();
        Deque<BlockState> stateQueue = Queues.newArrayDeque();
        Deque<FluidPipe> pipeQueue = Queues.newArrayDeque();

        PipeVertex startVertex = this.getPipeVertex(world, start, oldState);
        if (!startVertex.canSimplify())
        {
            return vertices;
        }

        visited.add(start);
        stack.add(start);
        pipeQueue.add(this);
        stateQueue.add(oldState);

        while (!stack.isEmpty())
        {
            BlockPos current = stack.poll();
            FluidPipe currentPipe = pipeQueue.poll();
            BlockState currentState = stateQueue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();

            for (Direction direction : currentPipe.getConnections(currentState, p -> true))
            {
                mutable.set(current, direction);

                if (visited.contains(mutable))
                    continue;

                BlockState offsetState = world.getBlockState(mutable);
                FluidPipe offsetPipe = FluidPipe.findFluidPipe(world, mutable, offsetState);
                if (offsetPipe != null)
                {
                    PipeVertex vertex = offsetPipe.getPipeVertex(world, mutable, offsetState);
                    if (vertex != null && !vertex.canSimplify())
                    {
                        vertices.add(Pair.of(vertex, direction));
                        continue;
                    }

                    visited.add(mutable.toImmutable());
                    stack.add(mutable.toImmutable());
                    stateQueue.add(offsetState);
                    pipeQueue.add(offsetPipe);
                }
            }
        }
        return vertices;
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
        if (world.getBlockEntity(pos) instanceof FluidPipeBlockEntity<?> be)
        {
            // TODO: remove this cast
            // What cast?
            be.getPipeVertex().updateNodes(world, pos.toImmutable(), state);
            return be.getPipeVertex();
        }
        return null;
    }

    default int countConnections(BlockState blockState)
    {
        return Iterables.size(getConnections(blockState, d -> true));
    }

    default PipeCol getCol(World world, BlockPos pos, BlockState blockState)
    {
        return PipeCol.ANY;
    }

    enum PipeCol
    {
        ANY(DyeColor.WHITE),
        WHITE(DyeColor.WHITE),
        ORANGE(DyeColor.ORANGE),
        MAGENTA(DyeColor.MAGENTA),
        LIGHT_BLUE(DyeColor.LIGHT_BLUE),
        YELLOW(DyeColor.YELLOW),
        LIME(DyeColor.LIME),
        PINK(DyeColor.PINK),
        GRAY(DyeColor.GRAY),
        LIGHT_GRAY(DyeColor.LIGHT_GRAY),
        CYAN(DyeColor.CYAN),
        PURPLE(DyeColor.PURPLE),
        BLUE(DyeColor.BLUE),
        BROWN(DyeColor.BROWN),
        GREEN(DyeColor.GREEN),
        RED(DyeColor.RED),
        BLACK(DyeColor.BLACK) ;

        private final DyeColor dyeColor;

        PipeCol(DyeColor dyeColor)
        {
            this.dyeColor = dyeColor;
        }

        public boolean matches(PipeCol other)
        {
            return this == ANY || other == ANY || this == other;
        }

        public int hexCode()
        {
            return dyeColor.getFireworkColor();
        }
    }
}
