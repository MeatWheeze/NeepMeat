package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.api.block.pipe.IFluidPipe;
import com.neep.neepmeat.transport.block.fluid_transport.IFluidNodeProvider;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
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
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class PipeNetwork
{
    private ServerWorld world;
    public final long uid; // Unique identifier for every network
    private final BlockPos origin;
    private final Direction originFace;
    public static int UPDATE_DISTANCE = 50;

    public static short TICK_RATE = 2;
    public static long BASE_TRANSFER = 10500 * TICK_RATE;

    public List<Supplier<FluidNode>> connectedNodes = new ArrayList<>();

    public final IndexedHashMap<BlockPos, PipeState> networkPipes = new IndexedHashMap<>();
    protected PipeState.FilterFunction[][] nodeMatrix = null;

    protected boolean isBuilt;


    // My pet memory leak.
    // TODO: Find a way to remove unloaded networks from this
    public static List<PipeNetwork> LOADED_NETWORKS = new ArrayList<>();

    private PipeNetwork(ServerWorld world, BlockPos origin, Direction direction)
    {
        this.world = world;
        this.origin = origin;
        this.originFace = direction;
        this.uid = nextUid();
        this.isBuilt = false;
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
            Runnable runnable = () ->
            {
                long t1 = System.nanoTime();
                this.isBuilt = false;

                connectedNodes.forEach((node) -> node.get().setNetwork(world, this));
                if (!validate())
                {
                    return;
                }

                try
                {
                    this.nodeMatrix = PipeBranches.getMatrix(world, connectedNodes, networkPipes);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                PipeBranches.displayMatrix(nodeMatrix);
                this.isBuilt = true;
                long t2 = System.nanoTime();
//                System.out.println("Rebuilt network in " + (t2 - t1) / 1000000 + "ms");
            };
//            NetworkRebuilding.getExecutor().execute(runnable);
            runnable.run();
        }
    }

    public boolean isBuilt()
    {
        return isBuilt;
    }

    public void setWorld(ServerWorld world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }

//    int compareNodes(FluidNode node1, FluidNode node2, FluidNode ref)
//    {
//        if (node1.getDistance(ref) == node2.getDistance(ref))
//        {
//            return 0;
//        }
//        return node1.getDistance(ref) < node2.getDistance(ref) ? -1 : 1;
//    }

    public static boolean validForInsertion(ServerWorld world, FluidNode node, Supplier<FluidNode> targetSupplier)
    {
        FluidNode targetNode;
        boolean check1 =  !(targetNode = targetSupplier.get()).equals(node)
                && targetSupplier.get() != null
                && targetSupplier.get().getStorage(world) != null;

        float h = node.getTargetY() - targetNode.getTargetY();
        double gravityFlowIn = h < -1 ? 0 : 0.1 * h;

        // Pass if the current node is extracting from others OR is inserting into others.
        boolean activePulling = node.getFlow(world) < 0;
        boolean activePushing = node.getFlow(world) > 0;
        boolean targetInsert = targetNode.getMode(world).canInsert();
        boolean targetExtract = targetNode.getMode(world).canExtract();

        return check1 && (activePulling && targetExtract || activePushing && targetInsert || gravityFlowIn != 0);
    }

    // This abomination is responsible for transferring the fluid from node to node
    public void tick()
    {
        if (!isBuilt())
            return;

        long startTim = System.nanoTime();
        for (int i = 0; i < connectedNodes.size(); i++)
        {
            Supplier<FluidNode> fromSupplier = connectedNodes.get(i);
            Transaction transaction = Transaction.openOuter();
            FluidNode node;
            if ((node = fromSupplier.get()) == null || fromSupplier.get().getStorage(world) == null
                    || !fromSupplier.get().isStorage
//                    || !fromSupplier.get().getMode(world).isDriving()
            )
            {
                transaction.abort();
                continue;
            }

            // Adjust base flow if this node is in a capillary pipe
            long baseTransfer = networkPipes.get(node.getPos()).isCapillary() ? BASE_TRANSFER / 4 : BASE_TRANSFER;

            // Prevent unpredictable distribution
            long amount = node.firstAmount(world, transaction);
            long capacity = node.firstCapacity(world, transaction);
            long outBaseFlow = Math.min(baseTransfer, amount);
            long inBaseFlow = Math.min(baseTransfer, capacity);
            transaction.abort();

            // Filter out nodes that will cause crashes or are unnecessary for the calculation
            List<Integer> safeIndices;
            List<Supplier<FluidNode>> safeNodes;
            try (Transaction t2 = Transaction.openOuter())
            {
                Predicate<Supplier<FluidNode>> predicate = (supplier -> validForInsertion(world, node, supplier));

                safeIndices = new ArrayList<>();
                for (int j = 0; j < connectedNodes.size(); j++)
                {
                    if (predicate.test(connectedNodes.get(j)))
                        safeIndices.add(j);
                }
            }

            double sumDist = safeIndices.stream().mapToDouble(idx -> 1f / FluidNode.exactDistance(connectedNodes.get(idx).get(), node)).sum();

            for (int j : safeIndices)
            {
                Supplier<FluidNode> targetSupplier = connectedNodes.get(j);
                FluidNode targetNode = targetSupplier.get();

                float h = node.getTargetY() - targetNode.getTargetY();
                double gravityFlowIn = h < -1 ? 0 : 0.1 * h;
                float flow = node.getFlow(world) - targetNode.getFlow(world);

                double L = FluidNode.exactDistance(node, targetNode);

                long amountMoved;
                double v1 = (1f / L) / (sumDist);
                if (flow + gravityFlowIn > 0)
                {
                    Transaction t3 = Transaction.openOuter();
                    final int finalI = i;
                    long Q = (long) Math.ceil(outBaseFlow * (flow + gravityFlowIn) * v1);
                    amountMoved = StorageUtil.move(node.getStorage(world), targetNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, Q) > 0, Q, t3);
                    t3.commit();
                }
                else if (flow + gravityFlowIn < 0)
                {
                    Transaction t3 = Transaction.openOuter();
                    final int finalI = i;
                    long Q = (long) Math.ceil(inBaseFlow * (flow + gravityFlowIn) * v1);
                    amountMoved = StorageUtil.move(targetNode.getStorage(world), node.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, - Q) > 0, - Q, t3);
                    t3.commit();
                }
            }
        }
        long endTime = System.nanoTime();
        long totalTime = (endTime - startTim) / 1000000;
//        System.out.println("World time: " + world.getTime() + "\t ID: " + uid + "\t Total: " + totalTime);
    }

    public void discoverNodes(BlockPos startPos, Direction face)
    {
        networkPipes.clear();
        Queue<BlockPos> pipeQueue = new LinkedList<>();
        connectedNodes.clear();

        // List of pipes to be searched in next iteration
        List<BlockPos> visited = new ArrayList<>();

        pipeQueue.add(startPos);
        networkPipes.put(startPos, new PipeState(world.getBlockState(startPos)));
        visited.add(startPos);

        int depth = 0;

        // Pipe search depth
//        for (int i = 0; i < UPDATE_DISTANCE; ++i)
//        {
//            for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
            while (!pipeQueue.isEmpty() && depth < UPDATE_DISTANCE)
            {
                ++depth;
                BlockPos current = pipeQueue.poll();
                BlockState state1 = world.getBlockState(current);

                if (!(state1.getBlock() instanceof IFluidPipe))
                    continue;

                for (Direction direction : ((IFluidPipe) state1.getBlock()).getConnections(state1, dir -> true))
                {
                    BlockPos next = current.offset(direction);
                    BlockState state2 = world.getBlockState(next);

                    if (!visited.contains(next))
                    {
                        if (state2.getBlock() instanceof IFluidPipe)
                        {
                            visited.add(next);
                            // Next block is connected in opposite direction
                            if (IFluidPipe.isConnectedIn(world, next, state2, direction.getOpposite()))
                            {
                                pipeQueue.add(next);
                                networkPipes.put(next, new PipeState(state2));
                            }
                        }
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
    }

    public void removeNode(NodePos pos)
    {
        Supplier<FluidNode> node = FluidNetwork.getInstance(world).getNodeSupplier(pos);
        connectedNodes.remove(FluidNetwork.getInstance(world).getNodeSupplier(pos));
        validate();
    }

    public enum UpdateReason
    {
        PIPE_BROKEN,
        PIPE_ADDED,
        CONNECTION_CHANGED,
        NODE_CHANGED,
        VALVE_CHANGED;
    }

    static
    {
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
        {
            LOADED_NETWORKS.clear();
        });
    }
}
