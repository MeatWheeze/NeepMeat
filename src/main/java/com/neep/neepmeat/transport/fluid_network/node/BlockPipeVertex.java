package com.neep.neepmeat.transport.fluid_network.node;

import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class BlockPipeVertex extends SimplePipeVertex
{
    protected final FluidPipeBlockEntity parent;
    protected final NodeSupplier[] nodes = new NodeSupplier[6];
    protected long[] velocity = new long[6];
    private final ObjectArrayList<PipeFlowComponent> components = new ObjectArrayList<>(6);

    public BlockPipeVertex(FluidPipeBlockEntity fluidPipeBlockEntity)
    {
        this.parent = fluidPipeBlockEntity;
        components.size(6);
    }

    @Override
    public void setNetwork(PipeNetwork network)
    {
        super.setNetwork(network);
        parent.setNetwork(network);
    }

    @Override
    public boolean canSimplify()
    {
        return super.canSimplify() && numNodes() == 0;
    }

    public int numNodes()
    {
        int number = 0;
        for (NodeSupplier node : nodes)
        {
            if (node != null) ++number;
        }
        return number;
    }

    public void updateNodes(ServerWorld world, BlockPos pos, BlockState state)
    {
        Arrays.fill(nodes, null);
        IFluidPipe.findFluidPipe(world, pos, state).ifPresent(p ->
        {
            for (Direction direction : p.getConnections(state, d -> true))
            {
                NodeSupplier node = FluidNodeManager.getInstance(world).getNodeSupplier(new NodePos(pos, direction));
                if (node.get() != null)
                {
                    nodes[direction.ordinal()] = node;
                }
            }
        });
    }

    protected float getHead(int dir)
    {
        return switch (dir)
        {
            case 0 -> elevationHead + pressureHead - 0.5f;
            case 1 -> elevationHead + pressureHead + 0.5f;
            default -> elevationHead + pressureHead;
        };
    }

    @Override
    public void tick()
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            components.clear();
            components.size(6);

            // The number of remaining transfers
            int transfers = 0;

            for (int dir = 0; dir < nodes.length; ++dir)
            {
                NodeSupplier node = nodes[dir];
                if (node != null && node.get() != null && getNodeInflux(node) >= 0)
                {
                    components.set(dir, node);
                    ++transfers;
                }
            }

            for (int dir = 0; dir < getAdjVertices().length; ++dir)
            {
                PipeVertex vertex = getAdjacent(dir);
                if (vertex != null && vertex.getTotalHead() - this.getTotalHead() <= 0)
                {
                    components.set(dir, vertex);
                    ++transfers;
                }
            }

            // Randomise transfer order to reduce opportunities for fluid to get stuck in loops.
            final int[] ints = parent.getWorld().getRandom().ints(0, 6).distinct().limit(6).toArray();

            for (int dir : ints)
            {
                PipeFlowComponent component = components.get(dir);
                if (component == null) continue;

                // Even if previous transfers failed, the remaining fluid should all be transferred.
                long transferAmount = (long) Math.min(amount, Math.ceil(amount / (float) transfers));

                long received = component.insert(dir, 0, transferAmount, (ServerWorld) parent.getWorld(), variant, transaction);

                amount -= received;
                if (amount <= 0) variant = FluidVariant.blank();

                --transfers;
            }
            transaction.commit();
        }
    }

    private int numOutputs()
    {
        int outputs = 0;
        for (PipeFlowComponent c : components) if (c != null) ++outputs;
        return outputs;
    }

    // Get the flow with respect to the node
    protected float getNodeInflux(NodeSupplier nodeSupplier)
    {
        float nodeFlow = nodeSupplier.get().getFlow();
//        float otherFlow = getTotalHead() - getHead(nodeSupplier.getPos().face().ordinal());
        float otherFlow = -getHead(nodeSupplier.getPos().face().ordinal()) < 0 ? -0.5f : 1; // TODO: Return something more sensible than 1
        return nodeFlow != 0 ? nodeFlow : otherFlow;
    }

    @Override
    public void preTick()
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            for (NodeSupplier nodeSupplier : nodes)
            {
                if (nodeSupplier == null) continue;
                FluidNode node = nodeSupplier.get();
                if (node == null) continue;

                Storage<FluidVariant> storage = node.getStorage((ServerWorld) parent.getWorld());

                float f = getNodeInflux(nodeSupplier);
                long transferAmount = (long) Math.ceil(f * FluidConstants.BUCKET / 8);
                if (transferAmount < 0)
                {
                    FluidVariant foundVariant = StorageUtil.findExtractableResource(storage, transaction);
                    long extracted;
                    if (foundVariant != null && (variant.isBlank() || foundVariant.equals(variant)))
                    {
                        long permittedAmount = canInsert((ServerWorld) parent.getWorld(), node.getNodePos().face().getOpposite().ordinal(), foundVariant, transferAmount);
                        extracted = storage.extract(foundVariant, -permittedAmount, transaction);
                        variant = foundVariant;
                        amount += extracted;
                    }
                }
            }
            transaction.commit();
        }
        super.preTick();
    }

    protected void checkBlocked()
    {
        components.clear();
        components.size(6);

        // The number of remaining transfers
        int transfers = 0;

        for (int dir = 0; dir < nodes.length; ++dir)
        {
            NodeSupplier node = nodes[dir];
            if (node != null && node.get() != null && getNodeInflux(node) >= 0)
            {
                components.set(dir, node);
                ++transfers;
            }
        }

        for (int dir = 0; dir < getAdjVertices().length; ++dir)
        {
            PipeVertex vertex = getAdjacent(dir);
            if (vertex != null && vertex.getTotalHead() - this.getTotalHead() <= 0)
            {
                components.set(dir, vertex);
                ++transfers;
            }
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            for (int dir = 0; dir < components.size(); ++dir)
            {
                PipeFlowComponent component = components.get(dir);
                long transferred = component.insert(dir, component.getConnectionDir(this), 1, (ServerWorld) parent.getWorld(), variant, transaction);
            }
            transaction.abort();
        }
    }

    @Override
    public long[] getVelocity()
    {
        return velocity;
    }

    @Override
    public long insert(int fromDir, int toDir, long maxAmount, ServerWorld world, FluidVariant insertVariant, TransactionContext transaction)
    {
        return super.insert(fromDir, toDir, maxAmount, world, insertVariant, transaction);
    }

    @Override
    public boolean keepNetworkValid()
    {
        int count = 0;
        for (NodeSupplier nodeSupplier : nodes)
        {
            if (nodeSupplier != null && nodeSupplier.get() != null) ++count;
        }
        return count >= 2;
    }

    @Override
    public String toString()
    {
        StringBuilder adj = new StringBuilder();
        for (PipeVertex v : getAdjVertices()) {
            if (v != null) adj.append(System.identityHashCode(v)).append(", ");
        }
        return "Vertex@" + System.identityHashCode(this) + "{connection=" + adj + "nodes: " + Arrays.toString(nodes) + ", head:" + getTotalHead() + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        return super.equals(o);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static FluidVariant findExtractable(Storage<FluidVariant> storage, FluidVariant preferred, TransactionContext transaction)
    {
        for (StorageView<FluidVariant> view : storage.iterable(transaction))
        {
            if (preferred.isBlank())
            {
                if (!view.isResourceBlank()) return view.getResource();
            }
        }
        return FluidVariant.blank();
    }
}
