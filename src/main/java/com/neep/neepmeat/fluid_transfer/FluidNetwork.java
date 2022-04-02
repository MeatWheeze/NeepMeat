package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.blockentity.fluid.NodeContainerBlockEntity;
import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
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

    public List<FluidNode> queuedNodes = new ArrayList<>();
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

    public static void tickNetwork(ServerWorld world)
    {
        // A mysterious ConcurrentModificationException is thrown when using ListIterator::remove()
        List<FluidNode> removal = new ArrayList<>();
        List<FluidNode> queue = new ArrayList<>(WORLD_NETWORKS.get(world).queuedNodes);
        for (FluidNode node : queue)
        {
            node.loadDeferred(world);
            removal.add(node);
        }
        WORLD_NETWORKS.get(world).queuedNodes.removeAll(removal);

        NMFluidNetwork.LOADED_NETWORKS.forEach(NMFluidNetwork::tick);
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

    private void updateNode(NodePos pos, FluidNode node)
    {
        Map<NodePos, FluidNode> nodes = getOrCreateMap(pos.toChunkPos());
        FluidNode presentNode;
        if ((presentNode = nodes.get(pos)) != null)
        {
            node.setNetwork(presentNode.getNetwork());
        }
        nodes.put(pos, node);

        System.out.println("Node updated: " + nodes.get(pos));
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
            NMFluidNetwork.validateAll();
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

    public void updateNode(World world, NodePos pos, FluidNode node)
    {
        if (!(world instanceof ServerWorld serverWorld))
        {
            return;
        }
        updateNode(pos, node);
        validatePos(serverWorld, pos.pos);
    }

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

    // Extracts fluid nodes and adds them to the world's network
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

    public Supplier<FluidNode> getNodeSupplier(NodePos pos)
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
    }

    public static void registerEvents()
    {
        ServerTickEvents.END_WORLD_TICK.register(FluidNetwork::tickNetwork);
        ServerWorldEvents.LOAD.register(FluidNetwork::startWorld);
//        ServerWorldEvents.UNLOAD.register(((server, world1) -> System.out.println("UNLOAD -----------------------------------------------------------------")));
//        ServerChunkEvents.CHUNK_LOAD.registter
    }
}
