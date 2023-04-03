package com.neep.neepmeat.transport.fluid_network;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PipeNetGraph implements NbtSerialisable
{
    /*
        1. Convert blocks from the world into a graph.
        2. Optimise graph by swallowing vertices with two connections.
        2. Compute 'head' at each vertex.
        3. ???
        4. Profit

        Elevation head:
        - Find the lowest Y coordinate in the network and subtract it from the Ys of every node. Scaling may be necessary.

        Pressure head:
        - Pumps make this happen. Decrement each pump's contribution by a scaled unit with every block of distance. (?)

        BREAKING NEWS! The quantity that we're calling head no longer bears any resemblance to head in real life!
     */

    protected final ServerWorld world;
    protected final Long2ObjectOpenHashMap<PipeVertex> vertices = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectOpenHashMap<PipeVertex> allVertices = new Long2ObjectOpenHashMap<>();
    protected final Object2ObjectOpenHashMap<NodePos, FluidPump> pumps = new Object2ObjectOpenHashMap<>();
    protected final ArrayDeque<BlockPos> posQueue = new ArrayDeque<>(10);
//    protected final HashSet<NodeSupplier> connectedNodes = new HashSet<>();

    public PipeNetGraph(ServerWorld world)
    {
        this.world = world;
    }

    public void rebuild(BlockPos startPos)
    {
        reset();
        buildGraph(startPos);
    }

    private void reset()
    {
        vertices.clear();
        allVertices.clear();
        pumps.clear();
    }

    public void buildGraph(BlockPos startPos)
    {
        Set<Long> visited = Sets.newHashSet();
        BlockPos.Mutable mutable = startPos.mutableCopy();

        posQueue.clear();
        posQueue.add(startPos);

        BlockState startState = world.getBlockState(startPos);
        IFluidPipe startPipe = IFluidPipe.findFluidPipe(world, startPos, world.getBlockState(startPos)).orElse(null);
        if (startPipe == null) return;
        PipeVertex startVertex = startPipe.getPipeVertex(world, startPos, startState);
        startVertex.reset();
        allVertices.put(startPos.asLong(), startVertex);
        visited.add(startPos.asLong());

        while (!posQueue.isEmpty())
        {
            BlockPos current = posQueue.poll();
            BlockState currentState = world.getBlockState(current);
            PipeVertex currentPipe = allVertices.get(current.asLong());

            if (currentState.getBlock() instanceof IFluidPipe currentPipeBlock)
            {
                for (Direction direction : currentPipeBlock.getConnections(currentState, direction -> true))
                {
                    mutable.set(current, direction);

                    if (visited.contains(mutable.asLong())) continue;
                    visited.add(mutable.asLong());

                    findFluidPump(mutable, direction.getOpposite());

                    // Check for opposite connection in next pipe
                    BlockState nextState = world.getBlockState(mutable);
                    if (nextState.getBlock() instanceof IFluidPipe nextPipe)
                    {
                        // TODO: Replace this static method
                        if (!IFluidPipe.isConnectedIn(world, mutable, nextState, direction.getOpposite())) continue;

                        posQueue.add(mutable.toImmutable());

                        appendVertex(currentPipe, nextPipe, direction, mutable, nextState);
                    }
                }
            }
        }
    }

    protected void findFluidPump(BlockPos pos, Direction direction)
    {
        FluidPump pump = FluidPump.SIDED.find(world, pos, direction);
        if (pump != null)
        {
            pumps.put(new NodePos(pos, direction), pump);
        }
    }

    private void appendVertex(PipeVertex currentVertex, IFluidPipe nextPipe, Direction from, BlockPos nextPos, BlockState nextState)
    {
        PipeVertex nextVertex = nextPipe.getPipeVertex(world, nextPos, nextState);
        nextVertex.reset();

        // Create links
        currentVertex.setAdjVertex(from.ordinal(), nextVertex);
        nextVertex.setAdjVertex(from.getOpposite().ordinal(), currentVertex);

        allVertices.put(nextPos.asLong(), nextVertex);
    }

    public void minimiseGraph()
    {
        vertices.putAll(allVertices);
        ObjectIterator<Long2ObjectMap.Entry<PipeVertex>> it = vertices.long2ObjectEntrySet().fastIterator();
        while (it.hasNext())
        {
            PipeVertex toRemove = it.next().getValue();
            if (toRemove.canSimplify() && toRemove.collapseEdges())
            {
                it.remove();
            }
        }
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

        for (ObjectIterator<Long2ObjectMap.Entry<PipeVertex>> it = vertices.long2ObjectEntrySet().fastIterator(); it.hasNext();)
        {
            Long2ObjectMap.Entry<PipeVertex> entry = it.next();

            // Set elevation head relative to the lowest vertex in the network.
            int vertexY = BlockPos.unpackLongY(entry.getLongKey());
            entry.getValue().setHeight(vertexY - minY.intValue());
        }

        pumps.object2ObjectEntrySet().fastForEach(e ->
        {
            AcceptorModes mode = e.getValue().getMode();
            if (!mode.isDriving()) return;

            // When pulling, the effective height at the pump will be -16 and will go to zero further from the pump.
            int depth = mode == AcceptorModes.PULL ? -getPumpDepth() : getPumpDepth();

            HashSet<PipeVertex> visited = Sets.newHashSet();
            Queue<PipeVertex> queue = Queues.newArrayDeque();
            PipeVertex startVertex = allVertices.get(e.getKey().pos().offset(e.getKey().face()).asLong());
            startVertex.addHead(depth);
            queue.add(startVertex);
            visited.add(startVertex);

            while (!queue.isEmpty() && Math.abs(depth) != 0)
            {
                // Increment elevation if pulling, decrement if pushing
                depth += mode == AcceptorModes.PULL ? 1 : -1;
                PipeVertex current = queue.poll();
                for (PipeVertex next : current.getAdjVertices())
                {
                    if (next == null || visited.contains(next)) continue;

                    visited.add(next);

                    next.addHead(depth);
                    queue.add(next);
                }
            }
        });
    }

    public static int getPumpDepth()
    {
        return 15;
    }

    public Long2ObjectOpenHashMap<PipeVertex> getVertices()
    {
        return vertices;
    }

    private void removeVertex(long pos)
    {
        vertices.remove(pos);
        allVertices.remove(pos);
    }

    public PipeVertex getVertex(BlockPos pos)
    {
        return allVertices.get(pos.asLong());
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        NbtList posList = new NbtList();

        allVertices.long2ObjectEntrySet().forEach(e -> posList.add(writeVertex(e.getValue())));

        nbt.put("vertices", posList);

        allVertices.long2ObjectEntrySet().forEach(e -> e.getValue().setNetwork(null));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        reset();

        NbtList vertexList = nbt.getList("vertices", NbtType.COMPOUND);

        vertexList.forEach(c -> readVertex((NbtCompound) c));
    }

    protected NbtCompound writeVertex(PipeVertex vertex)
    {
        NbtCompound compound = new NbtCompound();
        compound.putLong("pos", vertex.getPos());

        NbtList adjacent = new NbtList();
        for (int i = 0; i < vertex.getAdjVertices().length; ++i)
        {
            NbtCompound compound1 = new NbtCompound();
            PipeVertex adj = vertex.getAdjVertex(i);
            if (adj == null)
            {
                compound1.putBoolean("present", false);
            }
            else
            {
                compound1.putBoolean("present", true);
                compound1.putLong("pos", adj.getPos());
            }
            adjacent.add(compound1);
        }
        compound.put("adjacent", adjacent);

        return compound;
    }

    protected PipeVertex readVertex(NbtCompound nbt)
    {
        long longPos = nbt.getLong("pos");
        BlockPos pos = BlockPos.fromLong(longPos);
        BlockState state = world.getBlockState(pos);
        IFluidPipe pipe = IFluidPipe.findFluidPipe(world, pos, state).orElse(null);

        for (Direction direction : Direction.values())
        {
            findFluidPump(pos, direction);
        }

        PipeVertex vertex = pipe.getPipeVertex(world, pos, state);

        allVertices.put(longPos, vertex);

        // Read the vertex's neighbours.
        NbtList adjList = nbt.getList("adjacent", NbtType.COMPOUND);
        for (int dir = 0; dir < adjList.size(); ++dir)
        {
            NbtCompound adjNbt = adjList.getCompound(dir);

            // There is no neighbour in this direction
            if (!adjNbt.getBoolean("present")) continue;

            // Try to quickly retrieve the adjacent vertex from the map.
            long adjLongPos = adjNbt.getLong("pos");
            PipeVertex adjacent = allVertices.get(adjLongPos);
            if (adjacent == null)
            {
                // If the vertex has not already been found, try to retrieve it from the world.
                BlockPos adjPos = BlockPos.fromLong(adjLongPos);
                BlockState adjState = world.getBlockState(adjPos);
                IFluidPipe adjacentPipe = IFluidPipe.findFluidPipe(world, adjPos, adjState).orElse(null);
                if (adjacentPipe == null) continue;

                adjacent = adjacentPipe.getPipeVertex(world, adjPos, adjState);
            }

            // If it still isn't found, ignore it. Hopefully it will go away.
            if (adjacent == null) continue;

            vertex.getAdjVertices()[dir] = adjacent;
        }

        return vertex;
    }
}