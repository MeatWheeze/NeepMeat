package com.neep.neepmeat.transport.fluid_network.node;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class BlockPipeVertex extends SimplePipeVertex implements NbtSerialisable
{
    protected final FluidPipeBlockEntity<?> parent;
    protected final NodeSupplier[] nodes = new NodeSupplier[6];
//    protected long[] velocity = new long[6];
    private final ObjectArrayList<PipeFlowComponent> components = new ObjectArrayList<>(6);

    public BlockPipeVertex(FluidPipeBlockEntity<?> fluidPipeBlockEntity)
    {
        super(fluidPipeBlockEntity.getPos().asLong());
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

    @Override
    public void reset()
    {
        parent.setNetwork(null);
        super.reset();
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
            case 0 -> getHeight() + pressureHeight - 0.5f;
            case 1 -> getHeight() + pressureHeight + 0.5f;
            default -> getHeight() + pressureHeight;
        };
    }

    protected float getHeight(int dir)
    {
        return switch (dir)
        {
            case 0 -> height - 0.5f;
            case 1 -> height + 0.5f;
            default -> height;
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
                if (node != null && node.get() != null && getNodeFlow(node) >= 0)
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

    // Get the flow with respect to the node
    protected float getNodeFlow(NodeSupplier nodeSupplier)
    {
        // Negative for influx, positive for efflux.
        float nodeFlow = nodeSupplier.get().getFlow();

//        float heightFlow = -getHead(nodeSupplier.getPos().face().ordinal()) < 0 ? -0.5f : 1; // TODO: Return something more sensible than 1
        // If a node is above this vertex, the height difference should be -0.5.
        float heightFlow = -(getHeight(nodeSupplier.getPos().face().ordinal()) - height) - (nodeSupplier.get().getPressureHeight() - pressureHeight);

        return nodeFlow != 0 ? nodeFlow : heightFlow;
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

                float f = getNodeFlow(nodeSupplier);

                // Calculate the amount with respect to this vertex.
                long transferAmount = - (long) Math.ceil(f * FluidConstants.BUCKET / 8);
                if (transferAmount > 0)
                {
                    FluidVariant foundVariant = StorageUtil.findExtractableResource(storage, transaction);
                    long extracted;
                    if (foundVariant != null && (variant.isBlank() || foundVariant.equals(variant)))
                    {
                        // Note the negative signs
                        long permittedAmount = canInsert((ServerWorld) parent.getWorld(), node.getNodePos().face().getOpposite().ordinal(), foundVariant, transferAmount);
                        extracted = storage.extract(foundVariant, permittedAmount, transaction);
                        variant = foundVariant;
                        amount += extracted;
                    }
                }
            }
            transaction.commit();
        }
        super.preTick();
    }

    @Override
    public void addHead(int h)
    {
        pressureHeight += h;

        for (NodeSupplier nodeSupplier : nodes)
        {
            if (nodeSupplier == null || !nodeSupplier.exists()) continue;
            FluidNode node = nodeSupplier.get();

            // Simulate an extra level of depth for each attached node.
            // If the effective height at this position is -14, all attached nodes will have an effective height of -13.
            node.setPressureHeight(pressureHeight - Math.signum(h) * 1);
        }
    }

    @Override
    public void setSaveState(SaveState saveState)
    {
//        parent.setSaveState(saveState);
    }

    @Override
    public SaveState getState()
    {
        return SaveState.LOADED;
//        return parent.state;
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}
