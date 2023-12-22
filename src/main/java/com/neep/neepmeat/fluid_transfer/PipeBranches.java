package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
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

    public static PipeState.FilterFunction[][] getMatrix(ServerWorld world, List<Supplier<FluidNode>> nodes, IndexedHashMap<BlockPos, PipeState> pipes)
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
                if ((filterFunction = shortestPath(world, start, end, pipes)) != null)
                {
//                    Function<Long, Long> function = followPath(world, start, end, distances, pipes);
                    matrix[i][j] = filterFunction;
//                    matrix[i][j] = PipeState::identity;
                }
                else
                {
                    matrix[i][j] = PipeState::zero;
                }
            }
        }
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

    /** It's rather inefficient and janky.
     * @return Resulting flow-limiting function of all special pipes in the shortest route. Null if there is no route between two nodes,
     */
    public static PipeState.FilterFunction shortestPath(ServerWorld world, NodePos start, NodePos end, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        Map<BlockPos, Integer> distances = new LinkedHashMap<>();
        List<Pair<PipeState.ISpecialPipe, Direction>> specials = new ArrayList<>();
        PipeState.FilterFunction flowFunc = PipeState::identity;
        Queue<BlockPos> queue = new LinkedList<>();
        List<BlockPos> visited = new ArrayList<>();
        Direction reverse = end.face.getOpposite();

        distances.put(end.pos, 0);
        queue.add(end.pos);

        // Early return if the end is blocked
        if (!pipes.get(end.pos).canFluidFlow(start.face.getOpposite(), world.getBlockState(end.pos)))
        {
            return null;
        }

        while (!queue.isEmpty())
        {
            BlockPos pos = queue.poll();
            int dist = distances.get(pos);

            if (pos.equals(start.pos))
            {
                return flowFunc;
            }

            visited.add(pos);
//            queue.remove();

            PipeState pipe = pipes.get(pos);

            for (Direction connection : pipe.connections)
            {
                BlockPos offset = pos.offset(connection);
                if (!visited.contains(offset) && pipes.get(offset) != null)
                {
                    // Check if pipe can transfer fluid in the opposite direction
                    PipeState offsetPipe = pipes.get(offset);
                    if (!offsetPipe.canFluidFlow(connection.getOpposite(), world.getBlockState(offset)))
                    {
                        continue;
                    }

                    if (offsetPipe.isSpecial())
                    {
//                        specials.add(new Pair<>(offsetPipe.getSpecial(), connection.getOpposite()));
                        flowFunc = flowFunc.andThen(offsetPipe.getSpecial().getFlowFunction(world, connection.getOpposite(), offset, world.getBlockState(offset)));
                    }

                    distances.put(offset, dist + 1);
                    queue.add(offset);
                    reverse = connection;
                }
            }
        }
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
