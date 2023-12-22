package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidNodeManager
{
    protected static final HashMap<ServerWorld, FluidNodeManager> WORLD_MANAGERS = new HashMap<>();

    protected final Long2ObjectMap<Map<NodePos, FluidNode>> chunkNodes = new Long2ObjectArrayMap<>();
    protected final ServerWorld world;

    public FluidNodeManager(ServerWorld world)
    {
        this.world = world;
    }

    public static FluidNodeManager getInstance(ServerWorld world)
    {
        return WORLD_MANAGERS.get(world);
    }

    public static FluidNodeManager getInstance(World world)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            return getInstance(serverWorld);
        }
        throw new IllegalStateException("FluidNodeManager can only exist on the logical server");
    }

    public static void removeStorageNodes(World world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            NodePos nodePos = new NodePos(pos, direction);
            getInstance((ServerWorld) world).removeNode(nodePos);
        }
    }

    protected static void create(ServerWorld world)
    {
        FluidNodeManager manager = WORLD_MANAGERS.get(world);
        if (manager == null)
        {
            manager = new FluidNodeManager(world);
            WORLD_MANAGERS.put(world, manager);
        }
    }

    private static void startWorld(MinecraftServer server, ServerWorld world)
    {
        create(world);
    }

    private static void stopWorld(MinecraftServer server, ServerWorld world)
    {
        WORLD_MANAGERS.clear();
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

    public boolean removeNode(NodePos pos)
    {
        Map<NodePos, FluidNode> nodes;
        if ((nodes = chunkNodes.get(pos.toChunkPos().toLong())) == null)
        {
            // No nodes
            return false;
        }
        if (nodes.get(pos) != null)
        {
            // Ensure that node is removed from connected networks
            nodes.get(pos).onRemove();
            nodes.remove(pos);
            return true;
        }

        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean updatePosition(World world, NodePos pos)
    {
        // Get connected storage, remove node if there isn't one
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos.facingBlock(), pos.face().getOpposite());
        if (storage == null)
        {
            if (get(pos) != null)
            {
                removeNode(pos);
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

        return newNode;
    }

    public List<FluidNode> getNodes(BlockPos pos)
    {
        List<FluidNode> list = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            FluidNode node = get(new NodePos(pos, direction));
            if (node != null)
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

    public void entityRemoved(BlockPos pos)
    {
        getNodes(pos).forEach(FluidNode::onRemove);
    }

    public void entityUnloaded(BlockPos pos)
    {
        List<FluidNode> nodes = getNodes(pos);

        nodes.forEach(FluidNode::onRemove);
    }

    public FluidNode get(NodePos nodePos)
    {
        return getOrCreateMap(nodePos.toChunkPos()).get(nodePos);
    }

    public NbtCompound writeNodes(BlockPos pos, NbtCompound nbt)
    {
        for (FluidNode node : getNodes(pos))
        {
            NbtCompound nodeNbt = new NbtCompound();
            nodeNbt = node.writeNbt(nodeNbt);
            nbt.put(node.getNodePos().face().toString(), nodeNbt);
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

    public static void registerEvents()
    {
        ServerWorldEvents.LOAD.register(FluidNodeManager::startWorld);
        ServerWorldEvents.UNLOAD.register(FluidNodeManager::stopWorld);
    }
}
