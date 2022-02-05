package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.blockentity.NodeContainerBlockEntity;
import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FluidNetwork
{
    public static final FluidNetwork INSTANCE = new FluidNetwork();

    public Map<ChunkPos, Map<NodePos, FluidNode>> chunkNodes = new HashMap<>();

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

//        System.out.println("Node updated: " + nodes.get(pos).get());
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

//    public void readNodes(ServerWorld world, BlockPos pos, NbtCompound nbt)
    public void readNodes(BlockPos pos, NbtCompound nbt)
    {
        List<FluidNode> nodes = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            System.out.println(direction);
            NbtElement nodeNbt = nbt.get(direction.toString());
            if (nodeNbt == null)
            {
                continue;
            }
            nodes.add(FluidNode.fromNbt((NbtCompound) nodeNbt));
            System.out.println("adding node");
        }
        putNodes(nodes, pos);
        System.out.println("put node");
    }

    public Supplier<FluidNode> getNodeSupplier(NodePos pos)
    {
        return new NodeSupplier(pos);
    }

    public static class NodeSupplier implements Supplier<FluidNode>
    {
        NodePos pos;

        public NodeSupplier(NodePos pos)
        {
            this.pos = pos;
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
            return INSTANCE.getOrCreateMap(pos.toChunkPos()).get(pos);
        }
    }
}
