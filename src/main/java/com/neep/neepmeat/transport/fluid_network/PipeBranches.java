package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.util.FluidPipeRouteFinder;
import com.neep.neepmeat.util.IndexedHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class PipeBranches extends HashMap<Long, SimplePipeVertex>
{
    public static void test(ServerWorld world, HashSet<Supplier<FluidNode>> nodes, IndexedHashMap<BlockPos, SimplePipeVertex> pipes)
    {
        if (nodes.size() > 1)
        {
            System.out.println("Yes!");
            List<Supplier<FluidNode>> list = nodes.stream().sequential().collect(Collectors.toList());

            NodePos start = list.get(0).get().getNodePos();
            NodePos end = list.get(1).get().getNodePos();
//            PipeState.FilterFunction distances = searchDFS(world, start, end, pipes);
//            System.out.println(distances);
        }
        else
        {
            System.out.println("No.");
        }
    }

    public static void displayMatrix(FilterFunction[][] matrix)
    {
        for (int i = 0; i < matrix.length; ++i)
        {
            for (int j = 0; j < matrix[0].length; ++j)
            {
                long out = matrix[i][j].applyVariant(FluidVariant.of(Fluids.WATER), PipeNetworkImpl1.BASE_TRANSFER);
                System.out.print(out + " ");
            }
            System.out.println();
        }
    }

    public static FilterFunction[][] getMatrix(ServerWorld world, List<NodeSupplier> nodes, IndexedHashMap<BlockPos, SimplePipeVertex> pipes)
    {
        int size = nodes.size();

        // Initialise matrix
        FilterFunction[][] matrix = (FilterFunction[][]) Array.newInstance(FilterFunction.class, size, size);
        FluidPipeRouteFinder finder = new FluidPipeRouteFinder(world, pipes);
        for (int i = 0; i < size; ++i)
        {
            for (int j = 0; j < size; ++j)
            {
                if (i == j)
                    matrix[i][j] = FilterFunction::zero;
                else
                    matrix[i][j] = FilterFunction::identity;
            }
        }

        try
        {
            NodeSupplier[] nodeArray = nodes.toArray(new NodeSupplier[0]);
            for (int i = 0; i < size; ++i)
            {
                Supplier<FluidNode> fromNode = nodeArray[i];
                if (fromNode.get() == null)
                    continue;

                for (int j = 0; j < size; ++j)
                {
                    Supplier<FluidNode> toNode = nodeArray[j];
                    if (toNode.get() == null || toNode.equals(fromNode))
                        continue;

                    NodePos start = fromNode.get().getNodePos();
                    NodePos end = toNode.get().getNodePos();

                    finder.init(start, end);
                    finder.loop(100);
                    FilterFunction function;
                    if (finder.hasResult() && (function = finder.getResult().right()) != null)
                    {
                        matrix[i][j] = function;
                    }
                    else
                    {
                        matrix[i][j] = FilterFunction::zero;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        long tt2 = System.nanoTime();
        return matrix;
    }
}
