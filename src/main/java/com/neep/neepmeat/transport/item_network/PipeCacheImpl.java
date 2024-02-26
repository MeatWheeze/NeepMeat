package com.neep.neepmeat.transport.item_network;

import com.neep.neepmeat.transport.api.PipeCache;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class PipeCacheImpl implements PipeCache
{
    protected final ServerWorld world;

//    protected final Map<BlockPos, ItemPipeInstance> pipes = new WeakHashMap<>();

    public PipeCacheImpl(ServerWorld world)
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
    public long route(BlockPos from, Direction in, BlockPos to, Direction out, ItemVariant variant, long amount, TransactionContext transaction)
    {
        ItemInPipe item = new ItemInPipe(null, null, variant, (int) amount, world.getTime());
        Stack<Direction> route = findPath(from, in, to, out, variant, amount);
        item.setRoute(route);
        return ItemPipeUtil.pipeToAny(item, from, in, world, transaction, false);
    }

    public ItemPipeInstance getPipe(BlockPos pos)
    {
//        return pipes.computeIfAbsent(pos, v -> createPipe(pos, world.getBlockState(pos)));
        return createPipe(pos, world.getBlockState(pos));
    }

    protected ItemPipeInstance createPipe(BlockPos pos, BlockState state)
    {
        if (state.getBlock() instanceof ItemPipe pipe)
        {
            return new ItemPipeInstance(pipe);
        }
        return null;
    }

    public ItemPipeInstance putPipe(BlockPos pos, ItemPipeInstance pipe)
    {
//        return pipes.put(pos, pipe);
        return null;
    }

    public void removePipe(BlockPos pos)
    {
//        pipes.remove(pos.asLong());
    }

    /** To be optionally called when an {@link ItemPipe} is added or changed.
     */
    @Deprecated
    public void onPipeAdded(ItemPipe pipe, BlockPos pos, BlockState state)
    {
//        ItemPipeInstance pipeState = new ItemPipeInstance(pipe);
//        putPipe(pos, pipeState);
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

    /** Removes the cached pipe at the given position. Must be called whenever an {@link ItemPipe} is removed.
     */
    @Deprecated
    public void onPipeRemove(BlockPos pos)
    {
//        removePipe(pos);
    }

    // TODO: Account for item filters
    public Stack<Direction> findPath(BlockPos startPos, Direction startDir, BlockPos endPos, Direction endDir, ItemVariant variant, long amount)
    {
        HashSet<Long> visited = new HashSet<>();
        Stack<BlockPos> posRoute = new Stack<>();
        Stack<Direction> route = new Stack<>();

        BlockPos current = startPos.offset(startDir);
        Direction currentDirection;

        while (!current.equals(endPos))
        {
            ItemPipeInstance pipe = getPipe(current);

            boolean nodeFound = false;
            Set<Direction> connections = pipe.pipe().getConnections(world.getBlockState(current), d -> true);
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

            if (route.empty())
                return route;

            // Remove from stack if stepping back
            current = posRoute.pop();
            currentDirection = route.pop();
        }

        posRoute.push(endPos);
        route.push(endDir);
//        System.out.println(route);

        return route;
    }

    public BlockPos nextNode(BlockPos lastIntersection, Set<Long> visited, Direction from, BlockPos endPos, Direction endDir)
    {
        BlockPos current = lastIntersection.offset(from).mutableCopy();
        Direction face = from;

        for (int i = 0; i < 100; ++i)
        {
            if (visited.contains(current.asLong()))
                return null;

            ItemPipeInstance pipe = getPipe(current);

            if (pipe == null)
                return null;

            if (!pipe.pipe().supportsRouting())
                return null;

            visited.add(current.asLong());
            Direction excluded = face.getOpposite();
            Set<Direction> connections = pipe.pipe().getConnections(world.getBlockState(current), d -> d != excluded);

            if (connections.size() == 0) return null;
            if (connections.size() > 1 || current.equals(endPos))
                return current.toImmutable();

            face = connections.iterator().next();
            current = current.offset(face);
        }
        return null;
    }

    private record ItemPipeInstance(ItemPipe pipe)
    {
    }
}
