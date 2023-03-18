package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.IndexedHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class PipeNetwork
{
    private ServerWorld world;
    public final UUID uuid;
    private final BlockPos origin;
    public static int UPDATE_DISTANCE = 50;

    public static short TICK_RATE = 1;
    public static long BASE_TRANSFER = 10500 * TICK_RATE;

    private final List<NodeSupplier> connectedNodes = new ObjectArrayList<>();

    public final IndexedHashMap<BlockPos, PipeState> networkPipes = new IndexedHashMap<>();
    protected PipeState.FilterFunction[][] nodeMatrix = null;

    protected boolean isBuilt;
    protected boolean isTicking;
    public boolean isSaved;

    // My pet memory leak.
    public static Set<PipeNetwork> LOADED_NETWORKS = new CopyOnWriteArraySet<>();

    private PipeNetwork(ServerWorld world, UUID uuid, BlockPos origin)
    {
        this.world = world;
        this.uuid = uuid;
        this.origin = origin;
        this.isBuilt = false;
    }

    public static Optional<PipeNetwork> tryCreateNetwork(ServerWorld world, BlockPos pos)
    {
//        System.out.println("trying fluid network at " + pos);
        UUID uuid = UUID.randomUUID();
        PipeNetwork network = new PipeNetwork(world, uuid, pos);
        network.rebuild(pos);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return Optional.of(network);
        }
//        System.out.println("fluid network failed");
        return Optional.empty();
    }

    public static Optional<PipeNetwork> createFromNbt(ServerWorld world, NbtCompound nbt)
    {
        UUID uuid = nbt.getUuid("uuid");
        BlockPos origin = NbtHelper.toBlockPos(nbt.getCompound("origin"));

        NbtList nbtNodes = nbt.getList("nodes", 10);
        ArrayList<NodeSupplier> nodes = new ArrayList<>();
        FluidNodeManager manager = FluidNodeManager.getInstance(world);
        PipeNetwork network = new PipeNetwork(world, uuid, origin);
        for (NbtElement element : nbtNodes)
        {
            // Type 10 list should contain NbtCompound
            NbtCompound compound = (NbtCompound) element;
            NodePos pos = NodePos.fromNbt(compound);

            // Set the node's network and trigger it to cache connected storages and pumps
            FluidNode node = manager.getNodeSupplier(pos).get();
            node.setNetwork(network, false);
            node.loadDeferred(world);

            nodes.add(manager.getNodeSupplier(pos));
        }
        network.connectedNodes.addAll(nodes);

        network.rebuild(origin);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return Optional.of(network);
        }
        NeepMeat.LOGGER.error("Pipe network creation from NBT failed. This is not intended behaviour.");

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
        boolean b = network.connectedNodes.equals(connectedNodes);
        return network.connectedNodes.equals(connectedNodes)
                && network.origin.equals(origin)
                && network.uuid.equals(uuid);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(uuid)
                .append(origin.hashCode())
                .build();
    }

    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("uuid", uuid);
        nbt.put("origin", NbtHelper.fromBlockPos(origin));

        NbtList list = new NbtList();
        for (NodeSupplier node : connectedNodes)
        {
            NbtCompound nodeNbt = new NbtCompound();
            node.pos.toNbt(nodeNbt);
            list.add(nodeNbt);
        }
        nbt.put("nodes", list);

        return nbt;
    }

    public boolean isValid()
    {
        if (connectedNodes.size() < 2)
            return false;

        return true;
    }

    // Removes network and connected nodes if not valid.
    public boolean validate()
    {
        if (!isValid())
        {
            LOADED_NETWORKS.remove(this);
            connectedNodes.forEach(nodeSupplier -> nodeSupplier.ifPresent(n -> n.setNetwork(null, false)));
            connectedNodes.clear();
            return false;
        }
        return true;
    }

    public void rebuild(BlockPos startPos)
    {
        if (!world.isClient)
        {
            discoverNodes(startPos);
            this.isBuilt = false;

            if (!validate())
            {
                return;
            }

            long t1 = System.nanoTime();

            // Claimed nodes must be updated with the new network after validation.
            connectedNodes.forEach((node) -> node.get().setNetwork(this, false));
            try
            {
                this.nodeMatrix = PipeBranches.getMatrix(world, connectedNodes, networkPipes);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            long t2 = System.nanoTime();
            this.isBuilt = true;
            PipeBranches.displayMatrix(nodeMatrix);
//                System.out.println("Rebuilt network in " + (t2 - t1) / 1000000 + "ms");
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

    public ServerWorld getWorld()
    {
        return world;
    }

    public static boolean validPair(ServerWorld world, FluidNode node, Supplier<FluidNode> targetSupplier)
    {
        return targetSupplier.get() != null && !targetSupplier.get().equals(node) && targetSupplier.get().getStorage(world) != null;
    }

    public static int sortNodes(FluidNode fromNode, FluidNode toNode)
    {
        float h = fromNode.getTargetY() - toNode.getTargetY();
        double gravityFlowIn = h < -0 ? 0 : 0.1 * h;

        // Pass if the current node is extracting from others OR is inserting into others.
        boolean activePulling = fromNode.getFlow() < 0;
        boolean activePushing = fromNode.getFlow() > 0;
        boolean targetInsert = toNode.getFlow() <= 0 && toNode.getMode().canInsert();
        boolean targetExtract = toNode.getFlow() >= 0 && toNode.getMode().canExtract();

        return activePushing && targetInsert ? 0 :
                activePulling && targetExtract ? 1 :
                gravityFlowIn > 0 && !activePulling && !activePushing ? 2 : 3;
    }

    public static int sortNodes2(FluidNode toNode)
    {
        boolean targetInsert = toNode.getFlow() <= 0 && toNode.getMode().canInsert();
        boolean targetExtract = toNode.getFlow() >= 0 && toNode.getMode().canExtract();

        return targetInsert ? 0 :
                targetExtract ? 1 : 2;
    }

    protected static boolean isNodeSafe(FluidNode fromNode, FluidNode toNode)
    {
        int sort = sortNodes(fromNode, toNode);
        if (fromNode.getFlow() > 0 && sort == 0) return true; // fromNode is pushing and toNode can accept
        if (fromNode.getFlow() < 0 && sort == 1) return true; // fromNode is pulling and toNode can donate
        return false;
    }

    // This abomination is responsible for transferring the fluid from node to node
    // I don't like looking at this or thinking about it.

    protected ArrayDeque<Integer> indexQueue = new ArrayDeque<>();
    protected List<Integer> insertIndices = new ArrayList<>();
    protected List<Integer> extractIndices = new ArrayList<>();

    public void tick()
    {
        this.isTicking = true;

        if (!isBuilt())
            return;

        validate();
        long startTim = System.nanoTime();
//        final NodeSupplier[] nodeArray;
//        synchronized (connectedNodes)
//        {
//            nodeArray = connectedNodes.toArray(new NodeSupplier[0])
//        }
        indexQueue.clear();
        indexQueue.addAll(IntStream.range(0, connectedNodes.size()).boxed().toList());

        insertIndices.clear();
        indexQueue.forEach(n ->
        {
            FluidNode node = connectedNodes.get(n).get();
            if (node.getFlow() <= 0 && node.getMode().canInsert()) insertIndices.add(n);
        });

        extractIndices.clear();
        indexQueue.forEach(n ->
        {
            FluidNode node = connectedNodes.get(n).get();
            if (node.getFlow() >= 0 && node.getMode().canExtract()) extractIndices.add(n);
        });


//        Map<Integer, List<Integer>> groups = IntStream.range(0, this.connectedNodes.size())
//                .boxed()
//                .collect(Collectors.groupingBy(n -> sortNodes2(connectedNodes.get(n).get())));
//
//        List<Integer> insertIndices = groups.get(0);
//        List<Integer> extractIndices = groups.get(1);


//        float f = fromNode.getFlow();
//        List<Integer> safeIndices;
//        if (fromNode.getFlow() > 0 && groups.containsKey(0)) safeIndices = groups.get(0);
//        else if (fromNode.getFlow() < 0 && groups.containsKey(1)) safeIndices = groups.get(1);
//        else if (fromNode.getFlow() == 0 && groups.containsKey(2)) safeIndices = groups.get(2);
//        else safeIndices = groups.get(3);

//        for (int i = 0; i < connectedNodes.size(); i++)
        while (!indexQueue.isEmpty())
        {
            int i = indexQueue.poll();
            Supplier<FluidNode> fromSupplier = connectedNodes.get(i);
            FluidNode fromNode = fromSupplier.get();
            if (fromNode == null || fromNode.getStorage(world) == null
                    || !fromSupplier.get().isStorage
                    || !fromNode.getMode().canFlow()
//                    || !fromSupplier.get().getMode().isDriving()
            )
            {
                continue;
            }

            // Adjust base flow if this fromNode is in a capillary pipe
            // TODO: Somehow avoid the mutable.toimuutable.aargh() thin
            long baseTransfer = networkPipes.get(fromNode.getPos().mutableCopy().toImmutable()).isCapillary() ? BASE_TRANSFER / 4 : BASE_TRANSFER;

            List<Integer> safeIndices = fromNode.getFlow() > 0.0 ? insertIndices : extractIndices;

            // Prevent unpredictable distribution
//            Transaction transaction = Transaction.openOuter();
//            long amount = fromNode.firstAmount(world, transaction);
//            long capacity = fromNode.firstCapacity(world, transaction);
//            long outBaseFlow = Math.min(baseTransfer, amount);
//            long inBaseFlow = Math.min(baseTransfer, capacity);
//            transaction.abort();

            // Split network nodes into groups
            // 0: safe for insertion
            // 1: safe for extraction
            // 2: gravity or something
            // 3: ???

//            double sumDist = safeIndices.stream().mapToDouble(idx -> 1f / FluidNode.exactDistance(nodeArray[idx].get(), fromNode)).sum();

            for (int j : indexQueue)
            {
                FluidNode toNode = connectedNodes.get(j).get();
                float flow = fromNode.getFlow() - toNode.getFlow();

                long Q = (long) Math.ceil(flow * baseTransfer / safeIndices.size());

                int finalI = i;
                if (flow > 0)
                {
                    StagedTransactions.queue(t ->
                            StorageUtil.move(fromNode.getStorage(world), toNode.getStorage(world), v ->
                            {
                                long l =  nodeMatrix[finalI][j].applyVariant(v, Q);
                                return l > 0;
                            }, Q, t));
                }
                else if (flow < 0)
                {
                   StagedTransactions.queue(t ->
                            StorageUtil.move(toNode.getStorage(world), fromNode.getStorage(world), v -> nodeMatrix[j][finalI].applyVariant(v, -Q) > 0, -Q, t));
                }

//                double L = FluidNode.exactDistance(fromNode, toNode);

//                double v1 = (1f / L) / (sumDist);
//                if (flow > 0 || gravityFlowIn > 0 && flow == 0)
//                {
//                    double q = flow > 0 ? flow : gravityFlowIn;
//                    final int finalI = i;
//                    long Q = (long) Math.ceil(Math.min(outBaseFlow, tInBaseFlow) * (q) * v1);
//                    StagedTransactions.queue(t ->
//                    {
//                        return StorageUtil.move(fromNode.getStorage(world), toNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, Q) > 0, Q, t);
//                    });
//                    StagedTransactions.TRANSACTIONS.poll().move(null);

//                     StorageUtil.move(fromNode.getStorage(world), toNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, Q) > 0, Q, null);
//                }
//                else if (flow < 0 || gravityFlowIn < 0 && flow == 0)
//                {
//                    double q = flow < 0 ? flow : gravityFlowIn;
//                    final int finalI = i;
//                    long Q = (long) Math.ceil(Math.min(inBaseFlow, tOutBaseFlow) * (q) * v1);
//                    StagedTransactions.queue(t ->
//                    {
//                        return StorageUtil.move(toNode.getStorage(world), fromNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, -Q) > 0, -Q, t);
//                    });
//                    StagedTransactions.TRANSACTIONS.poll().move(null);

//                    StorageUtil.move(toNode.getStorage(world), fromNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, - Q) > 0, - Q, null);
//                }
            }
        }
        long endTime = System.nanoTime();
        long totalTime = (endTime - startTim) / 1000000;
//        System.out.println("World time: " + world.getTime() + "\t ID: " + uid + "\t Total: " + totalTime);

        this.isTicking = false;
    }

    public void discoverNodes(BlockPos startPos)
    {
        networkPipes.clear();
        Queue<BlockPos> pipeQueue = new LinkedList<>();
        connectedNodes.clear();

        // Positions that have been checked
        Set<BlockPos> visited = new HashSet<>();

        pipeQueue.add(startPos);
        networkPipes.put(startPos, new PipeState(world.getBlockState(startPos)));
        visited.add(startPos);

        int depth = 0;

        while (!pipeQueue.isEmpty() && depth < UPDATE_DISTANCE)
        {
            ++depth;

            BlockPos current = pipeQueue.poll();
            BlockState state1 = world.getBlockState(current);

            if (!(state1.getBlock() instanceof IFluidPipe))
                continue;

            // Find the pipe at this position and check adjacent pipes it is connected to
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
                            PipeState nextPipe = new PipeState(state2);
                            PipeState currentPipe = networkPipes.get(current);
                            currentPipe.putAdjacent(direction, nextPipe);
                            nextPipe.putAdjacent(direction.getOpposite(), currentPipe);
                            networkPipes.put(next, nextPipe);
                        }
                    }
                    // TODO: remove
                    Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, next, direction.getOpposite());
                    if (storage != null)
                    {
                        NodeSupplier node = FluidNodeManager.getInstance(world).getNodeSupplier(new NodePos(current, direction));
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
        synchronized (connectedNodes)
        {
            connectedNodes.remove(FluidNodeManager.getInstance(world).getNodeSupplier(pos));
            validate();
        }
    }

    public List<NodeSupplier> getNodes()
    {
        return connectedNodes;
    }

//    public void notTicking()
//    {
//        if (this.isTicking) throw new IllegalStateException("Network was ticked in an incorrect state");
//    }

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
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
        {
            LOADED_NETWORKS.clear();
        });
    }
}
