package com.neep.neepmeat.transport.api.pipe.item_network;

import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.util.TubeUtils;
import com.neep.neepmeat.util.ItemInPipe;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ItemNetwork implements IItemNetwork
{
    protected final ServerWorld world;

    protected final Long2ObjectMap<ItemPipeState> pipes = new Long2ObjectArrayMap<>();

    public ItemNetwork(ServerWorld world)
    {
        this.world = world;
    }

    @Override
    public boolean retrieve(BlockPos to, Direction in, ItemVariant variant, long amount, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public long eject(BlockPos from, Direction out, ItemVariant variant, long amount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public long route(BlockPos from, Direction in, BlockPos to, Direction out, ItemVariant variant, int amount, TransactionContext transaction)
    {
        ItemInPipe item = new ItemInPipe(null, null, variant, amount, world.getTime());
        Stack<Direction> route = findPath(from, in, to, out, variant, amount);
        item.setRoute(route);
        return TubeUtils.pipeToAny(item, from, Direction.NORTH, world, transaction, false);
    }

    public ItemPipeState getPipe(BlockPos pos)
    {
        return pipes.compute(pos.asLong(), (k, v) ->
                v != null ? v : createPipe(pos, world.getBlockState(pos)));
    }

    protected ItemPipeState createPipe(BlockPos pos, BlockState state)
    {
        if (state.getBlock() instanceof IItemPipe pipe)
        {
            ItemPipeState pipeState = new ItemPipeState(pipe);
            return pipeState;
        }
        return null;
    }

    public ItemPipeState putPipe(BlockPos pos, ItemPipeState pipe)
    {
        return pipes.put(pos.asLong(), pipe);
    }

    public void removePipe(BlockPos pos)
    {
        pipes.remove(pos.asLong());
    }

    /** To be optionally called when an {@link IItemPipe} is added or changed.
     */
    public void onPipeAdded(IItemPipe pipe, BlockPos pos, BlockState state)
    {
        ItemPipeState pipeState = new ItemPipeState(pipe);
        putPipe(pos, pipeState);
//
//        List<Direction> connections = pipe.getConnections(state, d -> true);
//        for (Direction direction : connections)
//        {
//            BlockPos offset = pos.offset(direction);
//            ItemPipeState adjPipe = pipes.get(offset.asLong());
//            if (adjPipe != null)
//            {
//                pipeState.connected[direction.getId()] = adjPipe;
//                adjPipe.connected[direction.getOpposite().getId()] = pipeState;
//            }
//        }
    }

    /** Removes the cached pipe at the given position. Must be called whenever an {@link IItemPipe} is removed.
     */
    public void onPipeRemove(BlockPos pos)
    {
        removePipe(pos);
    }

    public static final int MAX_DEPTH = 10;

    public Stack<Direction> findPath(BlockPos startPos, Direction startDir, BlockPos endPos, Direction endDir, ItemVariant variant, long amount)
    {
        HashSet<Long> visited = new HashSet<>();
        Stack<BlockPos> posRoute = new Stack<>();
        Stack<Direction> route = new Stack<>();

        BlockPos current = startPos.offset(startDir);
        Direction currentDirection;

        while (!current.equals(endPos))
        {
            ItemPipeState pipe = getPipe(current);

            boolean nodeFound = false;
            List<Direction> connections = pipe.getPipe().getConnections(world.getBlockState(current), d -> true);
            for (Direction direction : connections)
            {
                BlockPos nextPos = nextNode(current, visited, direction, endPos, endDir);
                if (nextPos != null)
                {
                    // Step to next node
                    currentDirection = direction;

                    // Only push the initial direction if the starting pipe is a junction
                    if (!(current.equals(startPos.offset(startDir)) && connections.size() <= 2))
                        route.push(currentDirection);
                    posRoute.push(current);
                    visited.add(nextPos.asLong());
                    current = nextPos;
                    nodeFound = true;
                    break;
                }
            }
            if (nodeFound) continue;

            if (route.empty()) return route;

            // Remove from stack if stepping back
            current = posRoute.pop();
            currentDirection = route.pop();
        }

        posRoute.push(endPos);
        route.push(endDir);
        System.out.println(route);

        return route;
    }

    public BlockPos nextNode(BlockPos lastIntersection, Set<Long> visited, Direction from, BlockPos endPos, Direction endDir)
    {
        BlockPos current = lastIntersection.offset(from).mutableCopy();
        Direction face = from;

        for (int i = 0; i < 100; ++i)
        {
            if (visited.contains(current.asLong())) return null;

            ItemPipeState pipe = getPipe(current);

            if (pipe == null) return null;

            visited.add(current.asLong());
            Direction excluded = face.getOpposite();
            List<Direction> connections = pipe.getPipe().getConnections(world.getBlockState(current), d -> d != excluded);

            if (connections.size() == 0) return null;
            if (connections.size() > 1 || current.equals(endPos)) return current;

            face = connections.get(0);
            current = current.offset(face);
        }
        return null;
    }

    private static class ItemPipeState
    {
        private final IItemPipe pipe;
//        private final ItemPipeState[] connected = new ItemPipeState[6];

        public ItemPipeState(IItemPipe pipe)
        {
            this.pipe = pipe;
        }

        public IItemPipe getPipe()
        {
            return pipe;
        }
    }

    public static class Route
    {
        public static final byte NORTH = (byte) Direction.NORTH.getId();
        public static final byte EAST = (byte) Direction.EAST.getId();
        public static final byte SOUTH = (byte) Direction.SOUTH.getId();
        public static final byte WEST = (byte) Direction.WEST.getId();
        public static final byte UP = (byte) Direction.UP.getId();
        public static final byte DOWN = (byte) Direction.DOWN.getId();

        public static final byte[] DIRECTIONS = {NORTH, EAST, SOUTH, WEST, UP, DOWN};

        protected final Stack<Byte> path;

        protected Route(Stack<Direction> pathIn)
        {
            this.path = new Stack<>();
            pathIn.forEach(d -> path.add((byte) d.getId()));
        }
    }
}
