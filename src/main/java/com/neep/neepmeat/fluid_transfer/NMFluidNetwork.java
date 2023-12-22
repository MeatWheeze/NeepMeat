package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class NMFluidNetwork
{
    private World world;
    public final long uid; // Unique identifier for every network
    private BlockPos origin;
    private Direction originFace;
    public static int UPDATE_DISTANCE = 10;

    public HashSet<Supplier<FluidNode>> connectedNodes = new HashSet<>();

    private final Map<BlockPos, PipeState> networkPipes = new HashMap<>();
    private final List<BlockPos> pipeQueue = new ArrayList<>();

    // My pet memory leak.
    public static List<NMFluidNetwork> LOADED_NETWORKS = new ArrayList<>();

    private NMFluidNetwork(World world, BlockPos origin, Direction direction)
    {
        this.world = world;
        this.origin = origin;
        this.originFace = direction;
        this.uid = nextUid();
    }

    private static long currentUid = 0;

    public static long nextUid()
    {
        return ++currentUid;
    }

    public static Optional<NMFluidNetwork> tryCreateNetwork(World world, BlockPos pos, Direction direction)
    {
        System.out.println("trying fluid network at " + pos);
        NMFluidNetwork network = new NMFluidNetwork(world, pos, direction);
        network.rebuild(pos, direction);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return Optional.of(network);
        }
        System.out.println("fluid network failed");
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        return "\nFluidNetwork at " + (origin).toString();
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof NMFluidNetwork network))
        {
            return false;
        }
        return network.connectedNodes.equals(connectedNodes)
                && network.origin.equals(origin)
                && network.originFace.equals(originFace)
                && network.uid == uid;
    }

    public static void validateAll()
    {
        LOADED_NETWORKS.removeIf(current -> !current.isValid());
    }

    public boolean isValid()
    {
        if (connectedNodes.size() < 2)
            return false;

        int count = 0;
        for (Iterator<Supplier<FluidNode>> iterator = connectedNodes.iterator(); iterator.hasNext(); )
        {
            Supplier<FluidNode> supplier = iterator.next();
            if (supplier.get() == null)
            {
                iterator.remove();
                ++count;
            }
        }
//        System.out.println(count + " " + connectedNodes.size());
        return connectedNodes.size() - count >= 2;
    }

    // Removes network and connected nodes if not valid.
    public boolean validate()
    {
        if (!isValid())
        {
            LOADED_NETWORKS.remove(this);
            connectedNodes.clear();
            return false;
        }
        return true;
    }

    public void rebuild(BlockPos startPos, Direction face)
    {
        if (!world.isClient)
        {
            discoverNodes(startPos, face);
            connectedNodes.forEach((node) -> node.get().setNetwork((ServerWorld) world, this));
            if (!validate())
                return;
//            System.out.println(uid + " setting nodes");
//            buildPressures();
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

    public World getWorld()
    {
        return world;
    }

    int compareNodes(FluidNode node1, FluidNode node2, FluidNode ref)
    {
        if (node1.getDistance(ref) == node2.getDistance(ref))
        {
            return 0;
        }
        return node1.getDistance(ref) < node2.getDistance(ref) ? -1 : 1;
    }

    public void tick()
    {
        buildPressures();
        for (Supplier<FluidNode> supplier : connectedNodes)
        {
            FluidNode node;
            if ((node = supplier.get()) == null || supplier.get().getStorage((ServerWorld) world) == null)
            {
                continue;
            }

            // Reorganise nodes so that closest come first.
            List<Supplier<FluidNode>> sorted = connectedNodes.stream().sorted((t1, t2) -> compareNodes(t1.get(), t2.get(), node)).collect(Collectors.toList());
            for (Supplier<FluidNode> targetSupplier : sorted)
            {
                FluidNode targetNode;
                if ((targetNode = targetSupplier.get()).equals(node) || targetSupplier.get() == null || supplier.get().getStorage((ServerWorld) world) == null)
                {
                    continue;
                }
                node.transmitFluid((ServerWorld) world, targetNode);
            }
        }
    }

    public void addNode(Supplier<FluidNode> node)
    {
        connectedNodes.add(node);
    }

    public void buildPressures()
    {
        try
        {
            for (Supplier<FluidNode> supplier : connectedNodes)
            {
                FluidNode node = supplier.get();
                if (node == null)
                {
                    continue;
                }

                List<BlockPos> nextSet = new ArrayList<>();
                networkPipes.values().forEach((segment) -> segment.setVisited(false));

                pipeQueue.clear();
                pipeQueue.add(node.getPos());

                for (int i = 0; i < UPDATE_DISTANCE; ++i)
                {
//                for (ListIterator<PipeSegment> iterator = networkPipes.listIterator(); iterator.hasNext();)
                    for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext(); )
                    {
                        BlockPos current = iterator.next();
                        networkPipes.get(current).setDistance(i + 1);
                        networkPipes.get(current).setVisited(true);
//                        System.out.println(networkPipes.get(current).getDistance() + ", " + networkPipes.get(current).connections);
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
                for (Supplier<FluidNode> supplier1 : connectedNodes)
                {
                    FluidNode targetNode = supplier1.get();
                    if (targetNode == null
                            || targetNode.equals(node)
                            || node.mode == AcceptorModes.NONE)
                    {
                        continue;
                    }
                    int distanceToNode = networkPipes.get(targetNode.getPos()).getDistance();
//                    System.out.print(node + ",\n " + distanceToNode + "\n");
                    node.distances.put(targetNode, distanceToNode);
//                    node.distances.put(node1, 1);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void discoverNodes(BlockPos startPos, Direction face)
    {
        networkPipes.clear();
        pipeQueue.clear();
        connectedNodes.clear();

        // List of pipes to be searched in next iteration
        List<BlockPos> nextSet = new ArrayList<>();
        List<BlockPos> visited = new ArrayList<>();

        pipeQueue.add(startPos);
        networkPipes.put(startPos, new PipeState(startPos, world.getBlockState(startPos)));

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

                    if (FluidAcceptor.isConnectedIn(state1, direction) && !visited.contains(next))
                    {
                        visited.add(next);
//                        System.out.println(next);
                        // Check that target is a pipe and not a fluid block entity
                        if (state2.getBlock() instanceof FluidAcceptor
                                && !(state2.getBlock() instanceof FluidNodeProvider))
                        {
                            // Next block is connected in opposite direction
                            if (FluidAcceptor.isConnectedIn(state2, direction.getOpposite()))
                            {
                                nextSet.add(next);
                                networkPipes.put(next, new PipeState(next.toImmutable(), state2));
                            }
                        }
                        else if (state2.hasBlockEntity())
                        {
                            Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, next, direction.getOpposite());
                            if (storage != null)
                            {
                                Supplier<FluidNode> node = FluidNetwork.getInstance(world).getNodeSupplier(new NodePos(current, direction));
                                if (node.get() != null)
                                {
                                    connectedNodes.add(node);
                                }
                            }
                        }
                    }
                }
                iterator.remove();
            }
            pipeQueue.addAll(nextSet);
        }
//        validate();
//        System.out.println("targets: " + connectedNodes);
    }

    public void removeNode(NodePos pos)
    {
        Supplier<FluidNode> node = FluidNetwork.getInstance(world).getNodeSupplier(pos);
        connectedNodes.remove(FluidNetwork.getInstance(world).getNodeSupplier(pos));
        validate();
    }
}
