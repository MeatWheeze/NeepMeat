package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class FluidNetwork2
{
    private World world;

    private List<FluidNode> connectedNodes = new ArrayList<>();
//    private List<PipeSegment> networkPipes = new ArrayList<>();
    private Map<BlockPos, PipeSegment> networkPipes = new LinkedHashMap<>();
    private List<BlockPos> pipeQueue = new ArrayList<>();

    public FluidNetwork2(World world)
    {
        this.world = world;
    }

    public void rebuild(BlockPos startPos, Direction face)
    {
        discoverNodes(startPos, face);
        buildPressures();
        tick();
    }

    public void tick()
    {
        for (FluidNode node : connectedNodes)
        {
            for (FluidNode targetNode : connectedNodes)
            {
                if (targetNode.equals(node))
                    continue;
                node.transmitFluid(targetNode);
            }
        }
    }

    public void buildPressures()
    {
        System.out.println("pipes: " + networkPipes.keySet());
        for (FluidNode node : connectedNodes)
        {
            node.setNetwork(this);

            // add initial location to queue
            // iterate through queue
            //      calculate pressure based on distance (assuming that pressure drops to 0 at level 10)
            //      store pressure somehow
            //      get adjacent pipes
            //      add adjacent pipes to queue
            //      remove item from queue

            List<BlockPos> nextSet = new ArrayList<>();
            List<BlockPos> visited = new ArrayList<>();

            pipeQueue.clear();
            pipeQueue.add(node.getPos().offset(node.getFace()));

            for (int i = 0; i < 10; ++i)
            {
//                for (ListIterator<PipeSegment> iterator = networkPipes.listIterator(); iterator.hasNext();)
                for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
                {
                    BlockPos current = iterator.next();
                    networkPipes.get(current).setDistance(i + 1);
                    visited.add(current);
//                    System.out.println(current);
                    for (Direction direction : networkPipes.get(current).connections)
                    {
                        if (networkPipes.containsKey(current.offset(direction)) && !visited.contains(current.offset(direction)))
                        {
                            nextSet.add(current.offset(direction));

//                            networkPipes.get(current).addPressure(node.getPressure() * (10f - (float) i) / 10f);
//                            System.out.println(i);
                        }
                    }
                    iterator.remove();
                }
                pipeQueue.addAll(nextSet);
                nextSet.clear();
            }
//            System.out.println(networkPipes.values());

            // TODO: optimise further
            for (FluidNode node1 : connectedNodes)
            {
                if (node1.equals(node))
                {
                    continue;
                }
                int distanceToNode = networkPipes.get(node1.getPos().offset(node1.getFace())).getDistance();
                node.distances.put(node1, distanceToNode);
            }
        }
    }

    public void discoverNodes(BlockPos startPos, Direction face)
    {
        networkPipes.clear();
        pipeQueue.clear();
        connectedNodes.clear();

        // List of pipes to be searched in next iteration
        List<BlockPos> nextSet = new ArrayList<>();

        networkPipes.put(startPos.offset(face), new PipeSegment(startPos.offset(face), world.getBlockState(startPos.offset(face))));
//            visitedPipes.put(start, 10f);
        pipeQueue.add(startPos.offset(face));

        // Pipe search depth
        for (int i = 0; i < 10; ++i)
        {
            nextSet.clear();
            for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
            {
                BlockPos current = iterator.next();

                for (Direction direction : Direction.values())
                {
                    BlockPos next = current.offset(direction);
                    BlockState state1 = world.getBlockState(current);
                    BlockState state2 = world.getBlockState(next);

                    if (FluidAcceptor.isConnectedIn(state1, direction) && !networkPipes.containsValue(new PipeSegment(next)))
                    {
                        // Check that target is a pipe and not a fluid block entity
                        if (state2.getBlock() instanceof FluidAcceptor
                                && !(state2.getBlock() instanceof FluidNodeProvider))
                        {
                            // Next block is connected in opposite direction
                            if (FluidAcceptor.isConnectedIn(state2, direction.getOpposite()))
                            {
                                nextSet.add(next);
                                networkPipes.put(next, new PipeSegment(next, state2));
                            }
                        }
                        else if (state2.getBlock() instanceof FluidNodeProvider)
                        {
                            FluidNodeProvider nodeProvider = (FluidNodeProvider) state2.getBlock();
                            if (nodeProvider.connectInDirection(state2, direction.getOpposite()))
                            {
//                                System.out.println("target: " + next.toString());
                                connectedNodes.add(nodeProvider.getNode(world, next, direction.getOpposite()));
                            }
                        }
                    }
                }
                iterator.remove();
            }
            pipeQueue.addAll(nextSet);
        }
        System.out.println("targets: " + connectedNodes);
    }

}
