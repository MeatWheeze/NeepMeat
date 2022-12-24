package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.lang.reflect.Array;
import java.util.*;
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
            PipeState.FilterFunction distances = searchDFS(world, start, end, pipes);
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

    public static PipeState.FilterFunction[][] getMatrix(ServerWorld world, Set<NodeSupplier> nodes, IndexedHashMap<BlockPos, PipeState> pipes)
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
                    PipeState.FilterFunction filterFunction;

                    if (/*(fromNode.get().isDriven() || toNode.get().isDriven()) &&*/ (filterFunction = searchDFS(world, start, end, pipes)) != null)
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

    /** It's rather inefficient and janky. It doesn't actually give the shortest path.
     * @return Resulting flow-limiting function of all special pipes in the shortest route. Null if there is no route between two nodes,
     */
    public static PipeState.FilterFunction searchDFS(ServerWorld world, NodePos start, NodePos end, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        // Reset visited flag on all pipes
        pipes.forEach(p -> p.flag = false);

        // Oh crumbs... so many deques...
        PipeState.FilterFunction flowFunc = PipeState::identity;
        Deque<BlockPos> posStack = new ArrayDeque<>();
        Deque<PipeState> pipeStack = new ArrayDeque<>();
        Deque<PipeState.FilterFunction> filterStack = new ArrayDeque<>();

        posStack.add(end.pos);
        pipeStack.add(pipes.get(end.pos));
        BlockPos current = end.pos;
        PipeState currentPipe = pipes.get(end.pos);
        Direction currentDir = end.face.getOpposite();
        currentPipe.flag = true;

        PipeState offsetPipe;
        BlockPos offset;
        boolean vertexFound;

        // Early return if the end is blocked.
        if (!processPipe(current, currentPipe, currentDir.getOpposite(), world, filterStack)) return null;

        while (!current.equals(start.pos))
        {
            vertexFound = false;

            // Push a non-visited vertex to the stack
            for (Direction connection : currentPipe.connections)
            {
                offset = current.offset(connection);
                offsetPipe = currentPipe.getAdjacent(connection);

                if (offsetPipe != null && !offsetPipe.flag)
                {
                    // Flag next pipe as visited
                    offsetPipe.flag = true;

                    if (!processPipe(offset, offsetPipe, connection.getOpposite(), world, filterStack)) continue;

                    posStack.push(current);
                    pipeStack.push(currentPipe);

                    current = offset;
                    currentPipe = offsetPipe;

                    vertexFound = true;

                    // Move to the new vertex, temporarily ignore all other connections
                    break;
                }
            }

            // Move to next vertex if one was found
            if (vertexFound) continue;

            current = posStack.pop();
            currentPipe = pipeStack.pop();
            filterStack.pop();
        }

        for (PipeState.FilterFunction function : filterStack)
        {
            flowFunc = flowFunc.andThen(function);
        }
        return flowFunc;
    }

    private static boolean processPipe(BlockPos pos, PipeState pipe, Direction direction, World world, Deque<PipeState.FilterFunction> filterStack)
    {
        // Check if pipe can transfer fluid in the opposite direction
        BlockState currentState = world.getBlockState(pos);
        if (!pipe.canFluidFlow(direction, currentState))
        {
            return false;
        }

        // Check if pipe has a special flow function.
        if (pipe.isSpecial())
        {
            filterStack.push(pipe.getSpecial().getFlowFunction(world, direction, pos, currentState));
        }
        else filterStack.push(PipeState::identity);
        return true;
    }
}
