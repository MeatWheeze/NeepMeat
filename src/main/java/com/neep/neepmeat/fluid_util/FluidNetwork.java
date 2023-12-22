package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.level.storage.AnvilLevelStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    public void removeNode(NodePos pos, FluidNode node)
    {
        Map<NodePos, FluidNode> nodes = getOrCreateMap(pos.toChunkPos());
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
        }
        NMFluidNetwork.validateAll();
    }

    public Supplier<FluidNode> getNodeSupplier(NodePos pos)
    {
        return (() -> getOrCreateMap(pos.toChunkPos()).get(pos));
    }
}
