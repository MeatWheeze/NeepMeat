package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.block.pipe.IFluidPipe;
import com.neep.neepmeat.block.fluid_transport.IFluidNodeProvider;
import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.util.FilterUtils;
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
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class PipeNetwork
{
    private ServerWorld world;
    public final long uid; // Unique identifier for every network
    private BlockPos origin;
    private Direction originFace;
    public static int UPDATE_DISTANCE = 10;

    public static short TICK_RATE = 2;
    public static long BASE_TRANSFER = 10500 * TICK_RATE;

    public List<Supplier<FluidNode>> connectedNodes = new ArrayList<>();

    public final IndexedHashMap<BlockPos, PipeState> networkPipes = new IndexedHashMap<>();
    protected PipeState.FilterFunction[][] nodeMatrix = null;


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
//            PipeBranches.test(world, connectedNodes, networkPipes);
            connectedNodes.forEach((node) -> node.get().setNetwork((ServerWorld) world, this));
            if (!validate())
            {
                return;
            }

            this.nodeMatrix = PipeBranches.getMatrix(world, connectedNodes, networkPipes);
            PipeBranches.displayMatrix(nodeMatrix);
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

    public static boolean validForInsertion(ServerWorld world, FluidNode node, Supplier<FluidNode> targetSupplier)
    {
        FluidNode targetNode;
        return !(targetNode = targetSupplier.get()).equals(node)
                && targetSupplier.get() != null
                && targetSupplier.get().getStorage(world) != null
                && targetNode.getMode(world).canInsert()
                && node.getMode(world).canExtract();
    }

    // This is responsible for transferring the fluid from node to node
    public void tick()
    {
        for (int i = 0; i < connectedNodes.size(); i++)
        {
            Supplier<FluidNode> fromSupplier = connectedNodes.get(i);
            Transaction transaction = Transaction.openOuter();
            FluidNode node;
            if ((node = fromSupplier.get()) == null || fromSupplier.get().getStorage(world) == null
                    || !fromSupplier.get().isStorage || !node.canExtract(world, transaction))
            {
                transaction.abort();
                continue;
            }

            // Adjust base flow if this node is in a capillary pipe
            long baseTransfer = networkPipes.get(node.getPos()).isCapillary() ? BASE_TRANSFER / 4 : BASE_TRANSFER;

            // Prevent unpredictable distribution
            long amount = node.firstAmount(world, transaction);
            long outBaseFlow = Math.min(baseTransfer, amount);
            transaction.abort();

            // Filter out nodes that will cause crashes, or are unnecessary for the calculation

            List<Integer> safeIndices;
            List<Supplier<FluidNode>> safeNodes;
            try (Transaction t2 = Transaction.openOuter())
            {
                Predicate<Supplier<FluidNode>> predicate = ((Predicate<Supplier<FluidNode>>)
                        (supplier -> validForInsertion(world, node, supplier)))
                        .and(supplier -> supplier.get().canInsert(world, t2));

                safeIndices = new ArrayList<>();
                for (int j = 0; j < connectedNodes.size(); j++)
                {
                    if (predicate.test(connectedNodes.get(j)))
                        safeIndices.add(j);
                }

                // TODO: Get rid of this
                safeNodes = connectedNodes.stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
                t2.abort();
            }

            double r = 0.5d;

            // Geometrical solution for flow in branched pipes:
            // https://physics.stackexchange.com/questions/31852/flow-of-liquid-among-branches

            // Calculate sum_i(r^2 / L_i^2)
            double sumDist = safeNodes.stream()
                    .map(supplier1 -> supplier1.get().getTargetPos().getManhattanDistance(node.getTargetPos()))
                    .mapToDouble(integer -> Math.pow(r, 2) / integer).sum();

            for (int j : safeIndices)
            {
                Supplier<FluidNode> targetSupplier = connectedNodes.get(j);
                FluidNode targetNode = targetSupplier.get();

                float h = node.getTargetY() - targetNode.getTargetY();
                double gravityFlowIn = h < -1 ? 0 : 0.1 * h;
                float flow = node.getFlow(world) - targetNode.getFlow(world);

                int L = node.getTargetPos().getManhattanDistance(targetNode.getTargetPos());


                // Calculate amount of fluid to be transferred
                long Q;
                if (networkPipes.get(targetNode.getPos()).isCapillary())
                {
                    // Change the target node influx if connected with capillary pipe
                    long inBaseFlow = networkPipes.get(targetNode.getPos()).isCapillary() ?
                            Math.min(outBaseFlow, BASE_TRANSFER / 4) : outBaseFlow;

                    // Ignore distance, distribute fluid evenly
                    Q = (long) Math.ceil(inBaseFlow * (flow + gravityFlowIn) / safeIndices.size());
                }
                else
                {
                    // Take distance into account
                    Q = (long) Math.ceil(
                            outBaseFlow * (flow + gravityFlowIn)
                                    * ((Math.pow(r, 2) / L) / (sumDist + Math.pow(r, 2) / L))
                    );
                }

                // Apply corresponding thingy

//                Q = nodeMatrix[i][j].applyVariant(Q);

//                System.out.println(nodeMatrix[0][1].apply(100L));
//                System.out.println("i: " + i + ", j: " + j + ", Q: " + Q);

                long amountMoved;
                if (Q >= 0)
                {
                    Transaction t3 = Transaction.openOuter();
                    int finalI = i;
                    amountMoved = StorageUtil.move(node.getStorage(world), targetNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, Q) > 0, Q, t3);
                    t3.commit();
                }
            }
        }
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

        // Pipe search depth
        for (int i = 0; i < UPDATE_DISTANCE; ++i)
        {
//            for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
            while (!pipeQueue.isEmpty())
            {
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
                        // Check that target is a pipe and not a fluid block entity
                        if (state2.getBlock() instanceof IFluidPipe
                                && !(state2.getBlock() instanceof IFluidNodeProvider))
                        {
                            visited.add(next);
                            // Next block is connected in opposite direction
                            if (IFluidPipe.isConnectedIn(world, next, state2, direction.getOpposite()))
                            {
                                pipeQueue.add(next);
                                networkPipes.put(next, new PipeState(state2));
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
            }
        }
//        System.out.println("special: " + special + "state: " + state);
//        System.out.println(networkPipes);
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
