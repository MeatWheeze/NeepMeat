package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntArrayMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class PipeBranches extends HashMap<Long, PipeState>
{
    public static void test(ServerWorld world, HashSet<Supplier<FluidNode>> nodes, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        if (nodes.size() > 1)
        {
            System.out.println("Yes!");
            List<Supplier<FluidNode>> list = nodes.stream().sequential().collect(Collectors.toList());

            NodePos start = list.get(0).get().getNodePos();
            NodePos end = list.get(1).get().getNodePos();
            PipeState.FilterFunction distances = shortestPath(world, start, end, pipes);
            System.out.println(distances);
        }
        else
        {
            System.out.println("No.");
        }
    }

    public static void displayMatrix(PipeState.FilterFunction[][] matrix)
    {
        for (int i = 0; i < matrix.length; ++i)
        {
            for (int j = 0; j < matrix[0].length; ++j)
            {
                long out = matrix[i][j].applyVariant(FluidVariant.of(Fluids.WATER), PipeNetwork.BASE_TRANSFER);
                System.out.print(out + " ");
            }
            System.out.println();
        }
    }

    public static PipeState.FilterFunction[][] getMatrix(ServerWorld world, List<NodeSupplier> nodes, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        int size = nodes.size();

        // Initialise matrix
        PipeState.FilterFunction[][] matrix = (PipeState.FilterFunction[][]) Array.newInstance(PipeState.FilterFunction.class, size, size);
        for (int i = 0; i < size; ++i)
        {
            for (int j = 0; j < size; ++j)
            {
                if (i == j)
                    matrix[i][j] = PipeState::zero;
                else
                    matrix[i][j] = PipeState::identity;
            }
        }

        try
        {
            for (int i = 0; i < size; ++i)
            {
                Supplier<FluidNode> fromNode = nodes.get(i);
                if (fromNode.get() == null)
                    continue;

                for (int j = 0; j < size; ++j)
                {
                    Supplier<FluidNode> toNode = nodes.get(j);
                    if (toNode.get() == null || toNode.equals(fromNode))
                        continue;

                    NodePos start = fromNode.get().getNodePos();
                    NodePos end = toNode.get().getNodePos();
                    PipeState.FilterFunction filterFunction;

                    if (/*(fromNode.get().isDriven() || toNode.get().isDriven()) &&*/ (filterFunction = shortestPath(world, start, end, pipes)) != null)
                    {
                        matrix[i][j] = filterFunction;
                    }
                    else
                    {
                        matrix[i][j] = PipeState::zero;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        long tt2 = System.nanoTime();
//        System.out.println((tt2-tt1) / 1000000);
        return matrix;
    }

//    public static BiFunction<FluidVariant, Long, Long> collectFunctions(World world, List<Pair<PipeState.ISpecialPipe, Direction>> functions)
//    {
//        BiFunction<FluidVariant, Long, Long> flow = PipeState::identity;
//        for (Pair<PipeState.ISpecialPipe, Direction> pair : functions)
//        {
//            flow = flow.andThen(pair.getLeft().getFlowFunction(world, pair.getRight(), pos, world.getBlockState(pos)));
//        }
//    }

    public static IndexedHashMap<BlockPos, PipeState> removeDeadEnds(ServerWorld world, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        List<BlockPos> deadEnds = new ArrayList<>();
        IndexedHashMap<BlockPos, PipeState> clearRoutes = pipes.clone();

        // Detect dead ends
        for (int i = 0; i < pipes.size(); ++i)
        {
            // TODO: Account for pipes with forced connections
            if (pipes.get(i).connections.size() < 2)
            {
                deadEnds.add(pipes.getKey(i));
            }
        }

        // Fill in dead ends
        List<BlockPos> visited = new ArrayList<>();

        for (BlockPos end : deadEnds)
        {
            visited.clear();

            visited.add(end);
            BlockPos current = end; // Assign starting position
            PipeState currentState;

            do
            {
                visited.add(current);
                currentState = pipes.get(current);
                clearRoutes.remove(current); // Declare current position a dead end
//                world.spawnParticles(ParticleTypes.BARRIER, current.getX() + 0.5, current.getY() + 1, current.getZ() + 0.5, 10, 0, 0, 0, 0);

                // Advance position
                for (Direction connection : currentState.connections)
                {
                    BlockPos offset = current.offset(connection);
                    System.out.println(current + " " + connection + " " + visited.contains(offset));
                    if (!visited.contains(offset))
                    {
                        current = offset;
                        break;
                    }
                }
                System.out.println(current + ", " + pipes.get(current));
            }
            while (pipes.get(current) != null && pipes.get(current).connections.size() < 3);
        }
        return clearRoutes;
    }

    public static void findRoute(NodePos fromPos, NodePos toPos, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        List<BlockPos> frontier = new ArrayList<>();
        List<BlockPos> nextFrontier = new ArrayList<>();

        frontier.add(fromPos.pos);

        int depth = 100;
        for (int i = 0; i < depth; ++i)
        {
            for (BlockPos current : frontier)
            {
                PipeState currentPipe = pipes.get(current);

                if (currentPipe.isSpecial())
                {
                }

                for (Direction direction : currentPipe.connections)
                {
                    BlockPos offset = current.offset(direction);
                    if (pipes.get(offset) != null)
                    {
                        nextFrontier.add(offset);
                    }
                }
            }
            frontier.addAll(nextFrontier);
        }
    }

    /** It's rather inefficient and janky. It doesn't actually give the shortest path.
     * @return Resulting flow-limiting function of all special pipes in the shortest route. Null if there is no route between two nodes,
     */
    public static PipeState.FilterFunction shortestPath(ServerWorld world, NodePos start, NodePos end, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        long t1 = System.nanoTime();
        pipes.forEach(p -> p.flag = false);
        Long2IntMap distances = new Long2IntArrayMap();
        PipeState.FilterFunction flowFunc = PipeState::identity;
        Queue<BlockPos> posQueue = new LinkedList<>();
        Queue<PipeState> pipeQueue = new LinkedList<>();

        distances.put(end.pos.asLong(), 0);
        posQueue.add(end.pos);
        pipeQueue.add(pipes.get(end.pos));

        // Early return if the end is blocked
        if (!pipes.get(end.pos).canFluidFlow(start.face.getOpposite(), world.getBlockState(end.pos)))
        {
            return null;
        }

        while (!posQueue.isEmpty())
        {
            BlockPos pos = posQueue.poll();
            PipeState currentPipe = pipeQueue.poll();
            int dist = distances.get(pos.asLong());

            if (pos.equals(start.pos))
            {
                return flowFunc;
            }

            currentPipe.flag = true;

            PipeState offsetPipe;
            for (Direction connection : currentPipe.connections)
            {
                BlockPos offset = pos.offset(connection);
                offsetPipe = currentPipe.getAdjacent(connection);
//                PipeState offsetPipe = pipes.get(offset);

                long tt2 = System.nanoTime();
                if (offsetPipe != null && !offsetPipe.flag)
                {
                    // Check if pipe can transfer fluid in the opposite direction
                    BlockState offsetState = world.getBlockState(offset);
                    if (!offsetPipe.canFluidFlow(connection.getOpposite(), offsetState))
                    {
                        continue;
                    }

                    if (offsetPipe.isSpecial())
                    {
//                        specials.add(new Pair<>(offsetPipe.getSpecial(), connection.getOpposite()));
                        flowFunc = flowFunc.andThen(offsetPipe.getSpecial().getFlowFunction(world, connection.getOpposite(), offset, offsetState));
                    }

                    distances.put(offset.asLong(), dist + 1);
                    posQueue.add(offset);
                    pipeQueue.add(offsetPipe);
                }
            }
        }
        long t2 = System.nanoTime();
        return null;
    }

    public static Function<Long, Long> followPath(ServerWorld world, NodePos start, NodePos end, Map<BlockPos, Integer> distances, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        Function<Long, Long> flow = Function.identity();
        BlockPos pos = start.pos;
        Direction fromDir = start.face.getOpposite();

        int level = 0;
        while (!pos.equals(end.pos) && level < 10)
        {
            ++level;
            int dist = distances.get(pos);
            PipeState pipe = pipes.get(pos);

            PipeState.ISpecialPipe special;
            if ((special = pipe.getSpecial()) != null)
            {
//                flow = flow.andThen(special.getFlowFunction(fromDir, world.getBlockState(pos)));
            }

            for (Direction direction : pipe.connections)
            {
                BlockPos offset = pos.offset(direction);
                Integer dist2 = distances.get(offset);
                if (pipes.get(offset) != null && dist2 != null && dist2 < dist)
                {
                    pos = offset;
                    fromDir = direction;
                }
            }
        }
        return flow;
    }
}
