package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PipeBranches extends HashMap<Long, PipeState>
{
    // TODO: Move pipes into an ArrayList and map each BlockPos to an index
    public static void test(ServerWorld world, HashSet<Supplier<FluidNode>> nodes, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        if (nodes.size() > 1)
        {
            System.out.println("Yes!");
            List<Supplier<FluidNode>> list = nodes.stream().sequential().collect(Collectors.toList());
//            IndexedHashMap<BlockPos, PipeState> clearRoutes = removeDeadEnds(world, pipes);
//            System.out.println(clearRoutes);
//            for (BlockPos pos : clearRoutes.keySet())
//            {
//                world.spawnParticles(ParticleTypes.BARRIER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 10, 0, 0, 0, 0);
//            }

            NodePos start = list.get(0).get().getNodePos();
            NodePos end = list.get(1).get().getNodePos();
            Map<BlockPos, Integer> distances = shortestPath(start, end, pipes);
            System.out.println(distances);
//            long flowResult = followPath(world, start, end, 100, distances, pipes);
//            System.out.println(flowResult);
        }
        else
        {
            System.out.println("No.");
        }
    }

    public static Function<Long, Long>[][] getMatrix(List<Supplier<FluidNode>> nodes)
    {
        int size = nodes.size();

        Function<Long, Long>[][] matrix = (Function<Long, Long>[][]) Array.newInstance(Function.class, size, size);

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


            }
        }
        return matrix;
    }

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

    public static void enumeratePositions(BlockPos start, BlockPos end, IndexedHashMap<BlockPos, PipeState> pipes)
    {

    }

    public static void doThings(IndexedHashMap<BlockPos, PipeState> pipes)
    {
       for (int i = 0; i < pipes.size(); ++i)
       {
           PipeState.ISpecialPipe specialPipe;
           if ((specialPipe = pipes.get(i).getSpecial()) != null)
           {
           }
       }
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

    public static Map<BlockPos, Integer> shortestPath(NodePos start, NodePos end, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        Map<BlockPos, Integer> distances = new LinkedHashMap<>();
        Queue<BlockPos> queue = new LinkedList<>();
        List<BlockPos> visited = new ArrayList<>();

        distances.put(end.pos, 0);
        queue.add(end.pos);

        while (!queue.isEmpty())
        {
            BlockPos pos = queue.peek();
            int dist = distances.get(pos);

            if (pos.equals(start.pos))
            {
                return distances;
            }

            visited.add(pos);
            queue.remove();


            PipeState pipe = pipes.get(pos);


            for (Direction connection : pipe.connections)
            {
                BlockPos offset = pos.offset(connection);
                if (!visited.contains(offset) && pipes.get(offset) != null)
                {
                    distances.put(offset, dist + 1);
                    queue.add(offset);
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
//            System.out.println("current distance: " + dist);
            PipeState pipe = pipes.get(pos);

            PipeState.ISpecialPipe special;
            if ((special = pipe.getSpecial()) != null)
            {
                flow = flow.andThen(special.get(fromDir, world.getBlockState(pos)));
            }

            for (Direction direction : pipe.connections)
            {
                BlockPos offset = pos.offset(direction);
//                System.out.println("target distance: " + dist2);
                if (pipes.get(offset) != null && distances.get(offset) < dist)
                {
                    pos = offset;
                    fromDir = direction;
//                    System.out.println(pos + " " + end.pos);
                }
            }
        }
        return flow;
    }
}
