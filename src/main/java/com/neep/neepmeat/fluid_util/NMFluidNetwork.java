package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.fluid_util.node.FluidNode;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class NMFluidNetwork
{
    private World world;
    private BlockPos origin;
    private Direction originFace;
    public static int UPDATE_DISTANCE = 5;
    private HashSet<FluidNode> connectedNodes = new HashSet<>();

    private Map<BlockPos, PipeSegment> networkPipes = new HashMap<>();
    private List<BlockPos> pipeQueue = new ArrayList<>();

    // My pet memory leak.
    public static List<NMFluidNetwork> LOADED_NETWORKS = new ArrayList<>();
    public static HashSet<NMFluidNetwork> NETWORKS = new HashSet<>();

    static
    {
        ServerTickEvents.END_SERVER_TICK.register(NMFluidNetwork::tickNetwork);
    }

    public NMFluidNetwork(World world, BlockPos origin, Direction direction)
    {
        this.world = world;
        this.origin = origin;
        this.originFace = direction;
//        LOADED_NETWORKS.add(this);
    }

    public static Optional<NMFluidNetwork> createNetwork(World world, BlockPos pos, Direction direction)
    {
        NMFluidNetwork network = new NMFluidNetwork(world, pos, direction);
        network.rebuild(pos, direction);
        if (network.checkValid())
        {
            return Optional.of(network);
        }
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        return "\nFluidNetwork at " + (origin).toString();
    }

    private static void tickNetwork(MinecraftServer minecraftServer)
    {
        LOADED_NETWORKS.forEach(NMFluidNetwork::tick);
    }

    // Removes a node that is no longer connected.
    public void removeNode(FluidNode node)
    {
        connectedNodes.remove(node);
        checkValid();
    }

    public boolean checkValid()
    {
        return connectedNodes.size() != 0;
    }

    public void rebuild(BlockPos startPos, Direction face)
    {
        if (!world.isClient)
        {
            discoverNodes(startPos, face);
            buildPressures();
//            tick();
        }
    }

    public void merge(NMFluidNetwork network)
    {

    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public void tick()
    {
//        buildPressures();
//        rebuild(origin, originFace);
        for (FluidNode node : connectedNodes)
        {
//            System.out.println(node);
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
        try
        {
            // Set networks before updating distances.
            connectedNodes.forEach((node) -> node.setNetwork(this));

            for (FluidNode node : connectedNodes)
            {
                List<BlockPos> nextSet = new ArrayList<>();
                networkPipes.values().forEach((segment) -> segment.setVisited(false));

                pipeQueue.clear();
                pipeQueue.add(node.getPos().offset(node.getFace()));

                for (int i = 0; i < UPDATE_DISTANCE; ++i)
                {
//                for (ListIterator<PipeSegment> iterator = networkPipes.listIterator(); iterator.hasNext();)
                    for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext(); )
                    {
                        BlockPos current = iterator.next();
                        networkPipes.get(current).setDistance(i + 1);
                        networkPipes.get(current).setVisited(true);
                        for (Direction direction : networkPipes.get(current).connections)
                        {
                            if (networkPipes.containsKey(current.offset(direction)) && !networkPipes.get(current.offset(direction)).isVisited())
//                            if (networkPipes.containsKey(current.offset(direction)) && !visited.contains(current.offset(direction)))
                            {
                                nextSet.add(current.offset(direction));
                            }
                        }
                        iterator.remove();
                    }
                    pipeQueue.addAll(nextSet);
                    nextSet.clear();
                }

                // TODO: optimise further
                for (FluidNode node1 : connectedNodes)
                {
                    if (node1.equals(node) || node1.mode == AcceptorModes.NONE)
                    {
                        continue;
                    }
                    int distanceToNode = networkPipes.get(node1.getPos().offset(node1.getFace())).getDistance();
                    node.distances.put(node1, distanceToNode);
//                    node.distances.put(node1, 1);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
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
        pipeQueue.add(startPos.offset(face));

        // Pipe search depth
        for (int i = 0; i < UPDATE_DISTANCE; ++i)
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
                                networkPipes.put(next, new PipeSegment(next.toImmutable(), state2));
                            }
                        }
                        else if (state2.hasBlockEntity())
                        {
                            BlockApiCache<Storage<FluidVariant>, Direction> cache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, next);
                            Storage<FluidVariant> storage = cache.find(state2, direction.getOpposite());
                            if (storage != null)
                            {
//                                FluidNetwork.NETWORK.
//                                    FluidNode node;
//                                    if (state2.getBlock() instanceof FluidNodeProvider provider)
//                                    {
//                                        node = provider.getNode(world, next, direction);
//                                    }
//                                    else
//                                    {
//                                        node = new FluidNode(next, direction.getOpposite(), storage, AcceptorModes.INSERT_EXTRACT, 0);
//                                    }
//                                    connectedNodes.add(node);
                            }
                        }
                    }
                }
                iterator.remove();
            }
            pipeQueue.addAll(nextSet);
        }

        checkValid();
//        System.out.println("targets: " + connectedNodes);
    }

}
