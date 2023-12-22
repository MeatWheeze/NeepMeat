package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FluidNetwork
{
    public static final FluidNetwork NETWORK = new FluidNetwork();

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

    public void updateNode(NodePos pos, FluidNode node)
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

    public void removeNode(NodePos pos)
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

    public Supplier<FluidNode> getNodeSupplier(NodePos pos)
    {
//        return (() -> getOrCreateMap(pos.toChunkPos()).get(pos));
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
            return NETWORK.getOrCreateMap(pos.toChunkPos()).get(pos);
        }
    }
}
