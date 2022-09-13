package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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

    public List<NodeSupplier> connectedNodes = new ArrayList<>();

    public final IndexedHashMap<BlockPos, PipeState> networkPipes = new IndexedHashMap<>();
    protected PipeState.FilterFunction[][] nodeMatrix = null;

    protected boolean isBuilt;
    public boolean isSaved;

    // My pet memory leak.
    // TODO: Find a way to remove unloaded networks from this
    public static List<PipeNetwork> LOADED_NETWORKS = new ArrayList<>();

    private PipeNetwork(ServerWorld world, UUID uuid, BlockPos origin)
    {
        this.world = world;
        this.uuid = uuid;
        this.origin = origin;
        this.isBuilt = false;
    }

    public static Optional<PipeNetwork> tryCreateNetwork(ServerWorld world, BlockPos pos)
    {
        System.out.println("trying fluid network at " + pos);
        UUID uuid = UUID.randomUUID();
        PipeNetwork network = new PipeNetwork(world, uuid, pos);
        network.rebuild(pos);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return Optional.of(network);
        }
        System.out.println("fluid network failed");
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
            node.setNetwork(network);
            node.loadDeferred(world);

            nodes.add(manager.getNodeSupplier(pos));
        }
        network.connectedNodes = nodes;

        network.rebuild(origin);
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
            Runnable runnable = () ->
            {
                this.isBuilt = false;

                connectedNodes.forEach((node) -> node.get().setNetwork(this));
                if (!validate())
                {
                    return;
                }

                long t1 = System.nanoTime();
                try
                {
                    this.nodeMatrix = PipeBranches.getMatrix(world, connectedNodes, networkPipes);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                long t2 = System.nanoTime();
//                PipeBranches.displayMatrix(nodeMatrix);
                this.isBuilt = true;
//                System.out.println("Rebuilt network in " + (t2 - t1) / 1000000 + "ms");
            };
//            NetworkRebuilding.getExecutor().execute(runnable);
            runnable.run();
//            StagedTransactions.getExecutor().execute(runnable);
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

    public static int sortNodes(ServerWorld world, FluidNode node, FluidNode targetNode)
    {
        float h = node.getTargetY() - targetNode.getTargetY();
        double gravityFlowIn = h < -0 ? 0 : 0.1 * h;

        // Pass if the current node is extracting from others OR is inserting into others.
        boolean activePulling = node.getFlow() < 0;
        boolean activePushing = node.getFlow() > 0;
        boolean targetInsert = targetNode.getFlow() <= 0 && targetNode.getMode().canInsert();
        boolean targetExtract = targetNode.getFlow() >= 0 && targetNode.getMode().canExtract();

        return activePushing && targetInsert ? 0 :
                activePulling && targetExtract ? 1 :
                gravityFlowIn > 0 && !activePulling && !activePushing ? 2 : 3;
    }

    // This abomination is responsible for transferring the fluid from node to node. I know it smells, it smells so badly
    // that I don't want to think about it.
    public void tick()
    {
        if (!isBuilt())
            return;

        validate();
        long startTim = System.nanoTime();
        for (int i = 0; i < connectedNodes.size(); i++)
        {
            Supplier<FluidNode> fromSupplier = connectedNodes.get(i);
            FluidNode node = fromSupplier.get();
            if (node == null || node.getStorage(world) == null
                    || !fromSupplier.get().isStorage
//                    || !fromSupplier.get().getMode(world).isDriving()
            )
            {
                continue;
            }

            // Adjust base flow if this node is in a capillary pipe
            long baseTransfer = networkPipes.get(node.getPos()).isCapillary() ? BASE_TRANSFER / 4 : BASE_TRANSFER;

            // Prevent unpredictable distribution
            Transaction transaction = Transaction.openOuter();
            long amount = node.firstAmount(world, transaction);
            long capacity = node.firstCapacity(world, transaction);
            long outBaseFlow = Math.min(baseTransfer, amount);
            long inBaseFlow = Math.min(baseTransfer, capacity);
            transaction.abort();

            // Split network nodes into groups
            // 0: safe for insertion
            // 1: safe for extraction
            // 2: gravity or something
            // 3: ???
            Map<Integer, List<Integer>> groups = IntStream.range(0, connectedNodes.size())
                    .filter(n -> validPair(world, node, connectedNodes.get(n)))
                    .boxed()
                    .collect(Collectors.groupingBy(n -> sortNodes(world, node, connectedNodes.get(n).get())));

            float f = node.getFlow();
            List<Integer> safeIndices;
            if (node.getFlow() > 0 && groups.containsKey(0)) safeIndices = groups.get(0);
            else if (node.getFlow() < 0 && groups.containsKey(1)) safeIndices = groups.get(1);
            else if (node.getFlow() == 0 && groups.containsKey(2)) safeIndices = groups.get(2);
            else safeIndices = groups.get(3);

            if (safeIndices == null)
                safeIndices = Collections.emptyList();

            double sumDist = safeIndices.stream().mapToDouble(idx -> 1f / FluidNode.exactDistance(connectedNodes.get(idx).get(), node)).sum();

            for (int j : safeIndices)
            {
                Supplier<FluidNode> targetSupplier = connectedNodes.get(j);
                FluidNode targetNode = targetSupplier.get();

                Transaction transaction1 = Transaction.openOuter();
                long tAmount = node.firstAmount(world, transaction);
                long tCapacity = node.firstCapacity(world, transaction);
                long tOutBaseFlow = Math.min(baseTransfer, tAmount);
                long tInBaseFlow = Math.min(baseTransfer, tCapacity);
                transaction1.abort();

                float h = node.getTargetY() - targetNode.getTargetY();
                double gravityFlowIn = h < -0 ? 0 : 0.1 * h;
                float flow = node.getFlow() - targetNode.getFlow();

                double L = FluidNode.exactDistance(node, targetNode);

                double v1 = (1f / L) / (sumDist);
//                if (flow + gravityFlowIn > 0)
                if (flow > 0 || gravityFlowIn > 0 && flow == 0)
                {
                    double q = flow > 0 ? flow : gravityFlowIn;
                    final int finalI = i;
                    long Q = (long) Math.ceil(Math.min(outBaseFlow, tInBaseFlow) * (q) * v1);
                    StagedTransactions.queue(t ->
                    {
                        return StorageUtil.move(node.getStorage(world), targetNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, Q) > 0, Q, t);
                    });
//                    StagedTransactions.TRANSACTIONS.poll().move(null);

//                     StorageUtil.move(node.getStorage(world), targetNode.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, Q) > 0, Q, null);
                }
                else if (flow < 0 || gravityFlowIn < 0 && flow == 0)
                {
                    double q = flow < 0 ? flow : gravityFlowIn;
                    final int finalI = i;
                    long Q = (long) Math.ceil(Math.min(inBaseFlow, tOutBaseFlow) * (q) * v1);
                    StagedTransactions.queue(t ->
                    {
                        return StorageUtil.move(targetNode.getStorage(world), node.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, - Q) > 0, - Q, t);
                    });
//                    StagedTransactions.TRANSACTIONS.poll().move(null);

//                    StorageUtil.move(targetNode.getStorage(world), node.getStorage(world), v -> nodeMatrix[finalI][j].applyVariant(v, - Q) > 0, - Q, null);
                }
            }
        }
        long endTime = System.nanoTime();
        long totalTime = (endTime - startTim) / 1000000;
//        System.out.println("World time: " + world.getTime() + "\t ID: " + uid + "\t Total: " + totalTime);
    }

    public void discoverNodes(BlockPos startPos)
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

        while (!pipeQueue.isEmpty() && depth < UPDATE_DISTANCE)
        {
            ++depth;
            BlockPos current = pipeQueue.poll();
            BlockState state1 = world.getBlockState(current);
//                PipeState pipeState

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
        connectedNodes.remove(FluidNodeManager.getInstance(world).getNodeSupplier(pos));
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
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
        {
            LOADED_NETWORKS.clear();
        });
    }
}
