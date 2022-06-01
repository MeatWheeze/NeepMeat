package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PipeBranches extends HashMap<Long, PipeState>
{
    public static void test(HashSet<Supplier<FluidNode>> nodes, Map<BlockPos, PipeState> pipes)
    {
        if (nodes.size() > 1)
        {
            System.out.println("Yes!");
            List<Supplier<FluidNode>> list = nodes.stream().sequential().collect(Collectors.toList());
//            findRoute(list.get(0).get().getNodePos(), list.get(1).get().getNodePos(), pipes);

        }
        else
        {
            System.out.println("No.");
        }
    }

    public static void findDeadEnds(HashMap<BlockPos, PipeState> pipes)
    {
        List<BlockPos> deadEnds = new ArrayList<>();
        Map<BlockPos, PipeState> clearRoutes = (Map<BlockPos, PipeState>) pipes.clone();

        // Detect dead ends
        for (Entry<BlockPos, PipeState> entry : pipes.entrySet())
        {
            if (entry.getValue().connections.size() < 2)
            {
                deadEnds.add(entry.getKey());
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

                // Advance position
                current = current.offset(currentState.connections.get(0));
            }
            while (pipes.get(current).connections.size() < 2 && !visited.contains(current));
        }
    }

    public static void findRoute(NodePos fromPos, NodePos toPos, Map<BlockPos, PipeState> pipes)
    {
        List<BlockPos> frontier = new ArrayList<>();

        frontier.add(fromPos.pos);

        for (ListIterator<BlockPos> it = frontier.listIterator(); it.hasNext();)
        {
            BlockPos current = it.next();
            for (Direction direction : pipes.get(current).connections)
            {
                BlockPos offset = current.offset(direction);
                if (pipes.get(offset) != null)
                {
                    it.add(offset);
                }
            }
        }
    }

//        BlockPos pos = fromPos.pos;
//        int distance = pos.getManhattanDistance(toPos.pos);
//        for (Direction direction : pipes.get(fromPos.pos).connections)
//        {
//             Find the adjacent position with the lowest distance
//            BlockPos offset = pos.offset(direction);
//            if (pipes.get(offset) != null && offset.getManhattanDistance(toPos.pos) <= distance)
//            {
//                distance = offset.getManhattanDistance(toPos.pos);
//                pos = offset;
//            }
//        }
}
