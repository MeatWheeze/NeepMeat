package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.function.Supplier;

/**
 * Represents deferred access to a node at a constant position. When a reference to a FluidNode must be retained across
 * multiple ticks, this should be used instead of a direct reference.
 */
public class NodeSupplier implements Supplier<FluidNode>
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
        return FluidNodeManager.getInstance(world).getOrCreateMap(pos.toChunkPos()).get(pos);
    }

    public boolean exists()
    {
        return get() != null;
    }
}
