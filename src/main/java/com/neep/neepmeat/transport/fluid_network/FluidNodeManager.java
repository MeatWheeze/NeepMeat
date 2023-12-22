package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.machine.fluid.NodeContainerBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.IFluidNodeProvider;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

public class FluidNodeManager
{
    protected static final HashMap<ServerWorld, FluidNodeManager> WORLD_NETWORKS = new HashMap<>();

    protected final Queue<FluidNode> queuedNodes = new LinkedList<>();
    protected final Long2ObjectMap<Map<NodePos, FluidNode>> chunkNodes = new Long2ObjectArrayMap<>();
    protected final ServerWorld world;

    public FluidNodeManager(ServerWorld world)
    {
        this.world = world;
    }

    public static FluidNodeManager getInstance(ServerWorld world)
    {
        return WORLD_NETWORKS.get(world);
    }

    public static FluidNodeManager getInstance(World world)
    {
        if (!(world instanceof ServerWorld))
        {
            return null;
        }
        return getInstance((ServerWorld) world);
    }

    public static void removeStorageNodes(World world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            NodePos nodePos = new NodePos(pos, direction);
            getInstance((ServerWorld) world).removeNode(world, nodePos);
        }
    }

    public boolean isPosLoaded(BlockPos pos)
    {
        boolean loaded = true;
        for (FluidNode node : getNodes(pos))
        {
            loaded = loaded && !node.needsDeferredLoading;
        }
        return loaded;
    }

    public void queueNode(FluidNode node)
    {
        if (world.getServer().isOnThread())
        {
            this.queuedNodes.add(node);
        }
    }

    public static boolean shouldTick(long worldTime)
    {
        return (worldTime % PipeNetwork.TICK_RATE) == 0;
    }

    public static void tickNetwork(ServerWorld world)
    {
        Queue<FluidNode> queue = WORLD_NETWORKS.get(world).queuedNodes;
        while (!queue.isEmpty())
        {
            FluidNode node = queue.poll();
            node.loadDeferred(world);
        }

        if (shouldTick(world.getTime()))
        {
            Runnable runnable = () ->
            {
                for (PipeNetwork network : PipeNetwork.LOADED_NETWORKS)
                {
                    if (network.getWorld().equals(world)) network.tick();
                }
            };
            StagedTransactions.getExecutor().execute(runnable);
        }
    }

    protected static void createNetwork(ServerWorld world)
    {
        FluidNodeManager network = WORLD_NETWORKS.get(world);
        if (network == null)
        {
            network = new FluidNodeManager(world);
            WORLD_NETWORKS.put(world, network);
        }
    }

    private static void startWorld(MinecraftServer server, ServerWorld world)
    {
        createNetwork(world);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(chunkNodes.hashCode())
                .append(world)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof FluidNodeManager network))
        {
            return false;
        }
        return network.world.equals(world)
                && network.chunkNodes.equals(chunkNodes);
    }

    public Map<NodePos, FluidNode> getOrCreateMap(ChunkPos chunkPos)
    {
        Map<NodePos, FluidNode> out;
        if ((out = chunkNodes.get(chunkPos.toLong())) != null)
        {
            return out;
        }
        chunkNodes.put(chunkPos.toLong(), out = new HashMap<>());
        return out;
    }

    private void removeNode(NodePos pos)
    {
        Map<NodePos, FluidNode> nodes;
        if ((nodes = chunkNodes.get(pos.toChunkPos().toLong())) == null)
        {
            return;
        }
        if (nodes.get(pos) != null)
        {
            // Ensure that node is removed from connected networks
            nodes.get(pos).onRemove();
            nodes.remove(pos);
        }
    }

    // Creates or retrieves a node block entity
    private BlockEntity getOrCreateBE(ServerWorld world, BlockPos pos)
    {
        BlockEntity be;
        if ((be = world.getBlockEntity(pos)) != null)
        {
            return be;
        }
        BlockState state = world.getBlockState(pos);
        world.addBlockEntity(new NodeContainerBlockEntity(pos, state));
        return world.getBlockEntity(pos);
    }

    private void removeBlockEntity(ServerWorld world, BlockPos pos)
    {
        world.removeBlockEntity(pos);
    }

    private boolean shouldPosHaveEntity(BlockPos pos)
    {
        return getNodes(pos).size() > 0;
    }

    private void validatePos(ServerWorld world, BlockPos pos)
    {
        if (shouldPosHaveEntity(pos))
        {
            getOrCreateBE(world, pos);
        }
        else
        {
            removeBlockEntity(world, pos);
        }
    }

    public boolean updatePosition(World world, NodePos pos)
    {
        if (!(world instanceof ServerWorld serverWorld))
        {
            return false;
        }

        // Get connected storage, remove node if there isn't one
        Storage<FluidVariant> storage;
        if ((storage = FluidStorage.SIDED.find(world, pos.facingBlock(), pos.face.getOpposite())) == null
                && !(world.getBlockState(pos.facingBlock()).getBlock() instanceof IFluidNodeProvider))
        {
            if (getNodeSupplier(pos).exists())
            {
                removeNode(world, pos);
                return true;
            }
            else
            {
                return false;
            }
        }

        Map<NodePos, FluidNode> nodes = getOrCreateMap(pos.toChunkPos());
        boolean newNode = false;
        FluidNode node;
        if (nodes.get(pos) == null)
        {
            // Create new node with params
            node = new FluidNode(pos, (ServerWorld) world);
            nodes.put(pos, node);
            newNode = true;
        }

        validatePos(serverWorld, pos.pos);

//        System.out.println("Node updated: " + nodes.get(pos));
        return newNode;
    }

//    public void replaceNode(World world, NodePos pos, FluidNode node)
//    {
//        if (!(world instanceof ServerWorld serverWorld))
//        {
//            return;
//        }
//        replaceNode(pos, node);
//        validatePos(serverWorld, pos.pos);
//    }

    public void removeNode(World world, NodePos pos)
    {
        if (!(world instanceof ServerWorld serverWorld))
        {
            return;
        }
        removeNode(pos);
        validatePos(serverWorld, pos.pos);
    }

    public List<FluidNode> getNodes(BlockPos pos)
    {
        List<FluidNode> list = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            FluidNode node;
            if ((node = getNodeSupplier(new NodePos(pos, direction)).get()) != null)
            {
                list.add(node);
            }
        }
        return list;
    }

    // Should only be called when saving or loading data
    private void putNodes(List<FluidNode> nodes, BlockPos pos)
    {
        Map<NodePos, FluidNode> map = getOrCreateMap(ChunkSectionPos.from(pos).toChunkPos());
        for (FluidNode node : nodes)
        {
            map.put(node.getNodePos(), node);
        }
    }

    /** Called whenever a node block entity is removed.
     * @param pos Position of node block entity
     */
    public void entityRemoved(BlockPos pos)
    {
        getNodes(pos).forEach(FluidNode::onRemove);
    }

    public void entityUnloaded(BlockPos pos)
    {
        List<FluidNode> nodes = getNodes(pos);
        PipeNetwork network = nodes.get(0).getNetwork();
        if (network == null || network.isSaved) return;

        NbtCompound nbt = network.toNbt();
        ((IServerWorld) world).getFluidNetworkManager().storeNetwork(network.uuid, nbt);
        network.isSaved = true;
        nodes.forEach(FluidNode::onRemove);
    }

    public NbtCompound writeNodes(BlockPos pos, NbtCompound nbt)
    {
        for (FluidNode node : getNodes(pos))
        {
            NbtCompound nodeNbt = new NbtCompound();
            nodeNbt = node.writeNbt(nodeNbt);
            nbt.put(node.getFace().toString(), nodeNbt);
        }
        return nbt;
    }

    // Extracts fluid nodes from nbt and adds them to the world's network
    public void readNodes(BlockPos pos, NbtCompound nbt, ServerWorld world)
    {
        List<FluidNode> nodes = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            NbtElement nodeNbt = nbt.get(direction.toString());
            if (nodeNbt == null)
            {
                continue;
            }
            nodes.add(FluidNode.fromNbt((NbtCompound) nodeNbt, world));
        }
        putNodes(nodes, pos);
    }

    public NodeSupplier getNodeSupplier(NodePos pos)
    {
        return new NodeSupplier(pos, world);
    }

    public static void registerEvents()
    {
        ServerTickEvents.START_WORLD_TICK.register(FluidNodeManager::tickNetwork);
        ServerWorldEvents.LOAD.register(FluidNodeManager::startWorld);
//        ServerWorldEvents.UNLOAD.register(((server, world1) -> System.out.println("UNLOAD -----------------------------------------------------------------")));
//        ServerChunkEvents.CHUNK_LOAD.registter
    }
}
