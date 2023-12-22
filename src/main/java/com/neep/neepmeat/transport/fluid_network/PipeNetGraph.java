package com.neep.neepmeat.transport.fluid_network;

import com.google.common.collect.Sets;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PipeNetGraph
{
    /*
        1. Convert blocks from the world into a graph.
        2. Optimise graph by swallowing vertices with two connections.
        2. Compute head at each vertex.
        3. ???
        4. Profit

        Elevation head:
        - Find the lowest Y coordinate in the network and subtract it from the Ys of every node. Scaling may be necessary.

        Pressure head:
        - Pumps make this happen. Decrement each pump's contribution by a scaled unit with every block of distance. (?)
     */

    protected final World world;
    protected final Long2ObjectOpenHashMap<SimplePipeVertex> vertices = new Long2ObjectOpenHashMap<>();
    protected final ArrayDeque<BlockPos> posQueue = new ArrayDeque<>(10);

    public PipeNetGraph(World world)
    {
        this.world = world;
    }

    public void rebuild(BlockPos startPos)
    {
        vertices.clear();

        buildGraph(startPos);
        minimiseGraph();
        calculateHead();
        vertices.forEach((k, v) -> System.out.print(BlockPos.fromLong(k) + ": " + v.toString() + "\n"));
    }

    public void buildGraph(BlockPos startPos)
    {
        Set<Long> visited = Sets.newHashSet();
        BlockPos.Mutable mutable = startPos.mutableCopy();

        posQueue.clear();
        posQueue.add(startPos);

        vertices.put(startPos.asLong(), new SimplePipeVertex(world.getBlockState(startPos)));

        while (!posQueue.isEmpty())
        {
            BlockPos current = posQueue.poll();
            BlockState currentState = world.getBlockState(current);
            SimplePipeVertex currentPipe = vertices.get(current.asLong());

            if (currentState.getBlock() instanceof IFluidPipe currentPipeBlock)
            {
                for (Direction direction : currentPipeBlock.getConnections(currentState, direction -> true))
                {
                    mutable.set(current, direction);

                    if (visited.contains(mutable.asLong())) continue;
                    visited.add(mutable.asLong());

                    // Check for opposite connection in next pipe
                    BlockState nextState = world.getBlockState(mutable);
                    if (nextState.getBlock() instanceof IFluidPipe nextPipe)
                    {
                        // TODO: Replace this static method
                        if (!IFluidPipe.isConnectedIn(world, mutable, nextState, direction.getOpposite())) continue;

                        posQueue.add(mutable.toImmutable());

                        addVertex(currentPipe, nextPipe, direction, mutable, nextState);
                    }
                }
            }
        }
    }

    private void addVertex(SimplePipeVertex currentVertex, IFluidPipe nextPipe, Direction from, BlockPos nextPos, BlockState nextState)
    {
        SimplePipeVertex nextVertex = nextPipe.createVertex(world, nextPos, nextState);

        // Create links
        currentVertex.putAdjacent(from, nextVertex);
        nextVertex.putAdjacent(from.getOpposite(), currentVertex);

        vertices.put(nextPos.asLong(), nextVertex);
    }

    public void minimiseGraph()
    {
        ObjectIterator<Long2ObjectMap.Entry<SimplePipeVertex>> it = vertices.long2ObjectEntrySet().fastIterator();
        while (it.hasNext())
        {
            SimplePipeVertex toRemove = it.next().getValue();
            if (canRemoveVertex(toRemove))
            {
                PipeVertex[] edge = new SimplePipeVertex[2];
                int current = 0;

                for (int i = 0; i < 6; ++i)
                {
                    if (toRemove.getAdjacentVertices()[i] != null)
                    {
                        edge[current] = toRemove.getAdjacentVertices()[i];
                        ++current;
                    }
                }

                // Determine the directions to link together.
                for (int i = 0; i < 6; ++i)
                {
                    if (edge[0].getAdjacentVertices()[i] == toRemove)
                    {
                        edge[0].putAdjacent(i, edge[1]);
                    }

                    if (edge[1].getAdjacentVertices()[i] == toRemove)
                    {
                        edge[1].putAdjacent(i, edge[0]);
                    }
                }

                it.remove();
            }
        }
    }

    protected boolean canRemoveVertex(SimplePipeVertex vertex)
    {
        return vertex.edges() == 2;
    }

    public void calculateHead()
    {
        // Find the minimum Y coordinate
        AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
        vertices.long2ObjectEntrySet().fastForEach(e ->
        {
            int y = BlockPos.unpackLongY(e.getLongKey());
            if (y < minY.get())
            {
                minY.set(y);
            }
        });

        for (ObjectIterator<Long2ObjectMap.Entry<SimplePipeVertex>> it = vertices.long2ObjectEntrySet().fastIterator(); it.hasNext();)
        {
            Long2ObjectMap.Entry<SimplePipeVertex> entry = it.next();

            // Set elevation head relative to the lowest vertex in the network.
            int vertexY = BlockPos.unpackLongY(entry.getLongKey());
            entry.getValue().setElevationHead(vertexY - minY.intValue());


        }
    }

    public Long2ObjectOpenHashMap<SimplePipeVertex> getVertices()
    {
        return vertices;
    }
}
