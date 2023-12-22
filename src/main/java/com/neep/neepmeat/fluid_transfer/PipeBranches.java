package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
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
            IndexedHashMap<BlockPos, PipeState> clearRoutes = findDeadEnds(world, pipes);
//            System.out.println(clearRoutes);
            for (BlockPos pos : clearRoutes.keySet())
            {
                world.spawnParticles(ParticleTypes.BARRIER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 10, 0, 0, 0, 0);
//                world.setBlockState(pos.add(0, 2, 0), Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
        else
        {
            System.out.println("No.");
        }
    }

    public static IndexedHashMap<BlockPos, PipeState> findDeadEnds(ServerWorld world, IndexedHashMap<BlockPos, PipeState> pipes)
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
            System.out.println(end);
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
}
