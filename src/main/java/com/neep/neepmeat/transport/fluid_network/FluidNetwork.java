package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.blockentity.fluid.NodeContainerBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.IFluidNodeProvider;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
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
import java.util.function.Supplier;

public class FluidNetwork
{
    protected static final HashMap<ServerWorld, FluidNetwork> WORLD_NETWORKS = new HashMap<>();

    public Queue<FluidNode> queuedNodes = new LinkedList<>();
    public final Map<ChunkPos, Map<NodePos, FluidNode>> chunkNodes = new HashMap<>();
    protected final ServerWorld world;

    protected FluidNetwork(ServerWorld world)
    {
        this.world = world;
    }

    public static FluidNetwork getInstance(ServerWorld world)
    {
        return WORLD_NETWORKS.get(world);
    }

    public static void removeStorageNodes(World world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            NodePos nodePos = new NodePos(pos, direction);
            getInstance((ServerWorld) world).removeNode(world, nodePos);
        }
    }

    public void queueNode(FluidNode node)
    {
        if (world.getServer().isOnThread())
        {
            this.queuedNodes.add(node);
        }
    }

    public static FluidNetwork getInstance(World world)
    {
        if (!(world instanceof ServerWorld))
        {
            return null;
        }
        return getInstance((ServerWorld) world);
    }

    public static boolean shouldTick(long worldTime)
    {
        return (worldTime & PipeNetwork.TICK_RATE) == 0;
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
            for (PipeNetwork network : PipeNetwork.LOADED_NETWORKS)
            {
                if (network.getWorld().equals(world)) network.tick();
            }
        }
    }

    protected static void createNetwork(ServerWorld world)
    {
        FluidNetwork network = WORLD_NETWORKS.get(world);
        if (network == null)
        {
            network = new FluidNetwork(world);
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
        if (!(o instanceof FluidNetwork network))
        {
            return false;
        }
        return network.world.equals(world)
                && network.chunkNodes.equals(chunkNodes);
    }

    public Map<NodePos, FluidNode> getOrCreateMap(ChunkPos pos)
    {
        Map<NodePos, FluidNode> out;
        if ((out = chunkNodes.get(pos)) != null)
        {
            return out;
        }
        chunkNodes.put(pos, out = new HashMap<>());
        return out;
    }

    // Replace the node at a position with a new one, preserving the network reference
    private void replaceNode(NodePos pos, FluidNode node)
    {
        Map<NodePos, FluidNode> nodes = getOrCreateMap(pos.toChunkPos());
        FluidNode presentNode;
        if ((presentNode = nodes.get(pos)) != null)
        {
            node.setNetwork(presentNode.getNetwork());
        }
        nodes.put(pos, node);

        System.out.println("Node replaced: " + nodes.get(pos));
    }

    private void removeNode(NodePos pos)
    {
        Map<NodePos, FluidNode> nodes;
        if ((nodes = chunkNodes.get(pos.toChunkPos())) == null)
        {
            return;
        }
        if (nodes.get(pos) != null)
        {
            // Ensure that node is removed from connected networks
            nodes.get(pos).onRemove();
            nodes.remove(pos);
            PipeNetwork.validateAll();
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

        System.out.println("Node updated: " + nodes.get(pos));
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

    public static class NodeSupplier implements Supplier<FluidNode>
    {
        NodePos pos;
        ServerWorld world;

        public NodeSupplier(NodePos pos, ServerWorld world)
        {
            this.pos = pos;
            this.world = world;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof NodeSupplier supplier))
            {
                return false;
            }
            return supplier.pos.equals(pos);
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder()
                    .append(pos.hashCode())
                    .toHashCode();
        }

        @Override
        public String toString()
        {
            return "provider for " + pos.toString();
        }

        @Override
        public FluidNode get()
        {
            return getInstance(world).getOrCreateMap(pos.toChunkPos()).get(pos);
        }

        public boolean exists()
        {
            return get() != null;
        }
    }

    public static void registerEvents()
    {
        ServerTickEvents.START_WORLD_TICK.register(FluidNetwork::tickNetwork);
        ServerWorldEvents.LOAD.register(FluidNetwork::startWorld);
//        ServerWorldEvents.UNLOAD.register(((server, world1) -> System.out.println("UNLOAD -----------------------------------------------------------------")));
//        ServerChunkEvents.CHUNK_LOAD.registter
    }
}
