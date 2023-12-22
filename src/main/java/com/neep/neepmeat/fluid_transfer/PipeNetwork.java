package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.block.IFluidPipe;
import com.neep.neepmeat.block.IFluidNodeProvider;
import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.util.FilterUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class PipeNetwork
{
    private ServerWorld world;
    public final long uid; // Unique identifier for every network
    private BlockPos origin;
    private Direction originFace;
    public static int UPDATE_DISTANCE = 10;

    public static long BASE_TRANSFER = 10500;

    public HashSet<Supplier<FluidNode>> connectedNodes = new HashSet<>();

    public final Map<BlockPos, PipeState> networkPipes = new HashMap<>();
    private final List<BlockPos> pipeQueue = new ArrayList<>();

    // My pet memory leak.
    // TODO: Find a way to remove unloaded networks from this
    public static List<PipeNetwork> LOADED_NETWORKS = new ArrayList<>();

    private PipeNetwork(ServerWorld world, BlockPos origin, Direction direction)
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

    public static Optional<PipeNetwork> tryCreateNetwork(ServerWorld world, BlockPos pos, Direction direction)
    {
        System.out.println("trying fluid network at " + pos);
        PipeNetwork network = new PipeNetwork(world, pos, direction);
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
        if (!(object instanceof PipeNetwork network))
        {
            return false;
        }
        return network.connectedNodes.equals(connectedNodes)
                && network.origin.equals(origin)
                && network.originFace.equals(originFace)
                && network.uid == uid;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(uid)
                .append(originFace.getId())
                .append(origin.hashCode())
                .build();
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

            buildPressures();
//            System.out.println(uid + " setting nodes");
//            buildPressures();
//            tick();
        }
    }

    public void merge(PipeNetwork network)
    {

    }

    public void setWorld(ServerWorld world)
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

    public static boolean validNode(ServerWorld world, FluidNode node, Supplier<FluidNode> targetSupplier)
    {
        FluidNode targetNode;
        if ((targetNode = targetSupplier.get()).equals(node)
                || targetSupplier.get() == null
                || targetSupplier.get().getStorage(world) == null
                || !targetSupplier.get().isStorage)
        {
            return false;
        }

        if (targetNode.getMode(world) == AcceptorModes.NONE || targetNode.getMode(world) == AcceptorModes.PUSH
                || node.getMode(world) == AcceptorModes.NONE
                )
        {
            return false;
        }
        return true;
    }

    public void tick()
    {
        for (Supplier<FluidNode> supplier : connectedNodes)
        {
            FluidNode node;
            if ((node = supplier.get()) == null || supplier.get().getStorage(world) == null
                    || !supplier.get().isStorage)
            {
                continue;
            }

            Transaction transaction = Transaction.openOuter();
            long amount = node.firstAmount(world, transaction);
            transaction.abort();

            long baseFlow = Math.min(BASE_TRANSFER, amount);

            Transaction t2 = Transaction.openOuter();
            List<Supplier<FluidNode>> safeNodes = connectedNodes.stream()
                    .filter(targetNode -> validNode(world, node, targetNode))
                    .filter(supplier1 -> supplier1.get().canInsert(world, t2))
                    .collect(Collectors.toList());
            t2.abort();

            double r = 0.5d;

            // Geometrical solution for flow in branched pipes:
            // https://physics.stackexchange.com/questions/31852/flow-of-liquid-among-branches

            // Calculate sum_i(r^2 / L_i^2)
            double sumDist = safeNodes.stream()
                    .map(supplier1 -> supplier1.get().getTargetPos().getManhattanDistance(node.getTargetPos()))
                    .mapToDouble(i -> Math.pow(r, 2) / i).sum();

            for (Supplier<FluidNode> targetSupplier : safeNodes)
            {
                FluidNode targetNode = targetSupplier.get();

                float h = node.getTargetY() - targetNode.getTargetY();
                double gravityFlowIn = h < -1 ? 0 : 0.1 * h;
                float flow = node.getFlow(world) - targetNode.getFlow(world);

                int L = node.getTargetPos().getManhattanDistance(targetNode.getTargetPos());
                long Q = (long) Math.ceil(
                        baseFlow * (flow + gravityFlowIn)
                        * ((Math.pow(r, 2) / L) / (sumDist))
                );

                long amountMoved;
                if (Q >= 0)
                {
                    Transaction t3 = Transaction.openOuter();
                    amountMoved = StorageUtil.move(node.getStorage(world), targetNode.getStorage(world), FilterUtils::any, Q, t2);
                    t3.commit();
                }
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

                for (Supplier<FluidNode> supplier1 : connectedNodes)
                {
                    FluidNode targetNode = supplier1.get();
                    if (targetNode == null
                            || targetNode.equals(node)
//                            || node.getMode(world) == AcceptorModes.NONE)
                    )
                    {
                        continue;
                    }
                    int distanceToNode = node.getNodePos().facingBlock().getManhattanDistance(targetNode.getNodePos().facingBlock());
                    PipeState pipe = networkPipes.get(targetNode.getPos());
//                    int distanceToNode = networkPipes.get(targetNode.getPos()).getDistance();
//                    System.out.print(node + ",\n " + distanceToNode + "\n");
                    node.distances.put(targetNode, 1);
//                    node.distances.put(targetNode, pipe.isCapillary() ? 1 : distanceToNode);
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

                    if (IFluidPipe.isConnectedIn(world, current, state1, direction) && !visited.contains(next))
                    {
                        visited.add(next);
//                        System.out.println(next);
                        // Check that target is a pipe and not a fluid block entity
                        if (state2.getBlock() instanceof IFluidPipe
                                && !(state2.getBlock() instanceof IFluidNodeProvider))
                        {
                            // Next block is connected in opposite direction
                            if (IFluidPipe.isConnectedIn(world, next, state2, direction.getOpposite()))
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
                        else if (state2.getBlock() instanceof IFluidNodeProvider)
                        {
                            Supplier<FluidNode> node = FluidNetwork.getInstance(world).getNodeSupplier(new NodePos(current, direction));
                            if (node.get() != null)
                            {
                                connectedNodes.add(node);
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

    static
    {
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
        {
            LOADED_NETWORKS.clear();
        });
    }
}
