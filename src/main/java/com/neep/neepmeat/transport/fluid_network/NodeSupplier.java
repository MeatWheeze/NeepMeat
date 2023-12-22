package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents deferred access to a node at a constant position. When a reference to a FluidNode must be retained across
 * multiple ticks, this should be used instead of a direct reference.
 */
public class NodeSupplier implements Supplier<FluidNode>, PipeFlowComponent
{
    protected NodePos pos;
    protected ServerWorld world;

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

    public NodePos getPos()
    {
        return pos;
    }

    @Override
    public String toString()
    {
        return "provider for " + pos.toString();
    }

    @Override
    public FluidNode get()
    {
        Map<NodePos, FluidNode> map = FluidNodeManager.getInstance(world).getOrCreateMap(pos.toChunkPos());
        return map.get(pos);
    }

    public void ifPresent(Consumer<FluidNode> consumer)
    {
        FluidNode node = get();
        if (get() != null)
        {
            consumer.accept(node);
        }
    }

    public boolean exists()
    {
        return get() != null;
    }

    @Override
    public long insert(int fromDir, int toDir, long maxAmount, ServerWorld world, FluidVariant variant, TransactionContext transaction)
    {
        FluidNode node = get();
        if (node != null)
        {
            return node.getStorage(world).insert(variant, maxAmount, transaction);
        }
        return 0;
    }
}
