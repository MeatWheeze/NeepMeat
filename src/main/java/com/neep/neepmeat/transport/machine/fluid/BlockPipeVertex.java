package com.neep.neepmeat.transport.machine.fluid;

import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.function.Supplier;

public class BlockPipeVertex extends SimplePipeVertex
{
    private final FluidPipeBlockEntity parent;
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
        parent.network = network;
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
        FluidTransport.findFluidPipe(world, pos, state).ifPresent(p ->
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

//        for (Direction direction : Direction.values())
//        {
//            PipeVertex adjacent = getAdjacent(direction.ordinal());
//
//            long u1 = amounts[direction.ordinal()];
//            long u2 = amounts[direction.getOpposite().ordinal()];
//
//            float dx = 1;
//
//            float uu_x = (long) ((u2 * u2 - u1 * u1) / (dx * 2));
//
//            float uv_y = ( (((u4 + uc) / 2) * ((va + vb) / 2)) - (((uc + u3) / 2) * ( (vc + vd) / 2))) / dy;
//        }


        try (Transaction transaction = Transaction.openOuter())
        {
            components.clear();
            components.size(6);
            int outputs = 0;
            for (int dir = 0; dir < nodes.length; ++dir)
            {
                NodeSupplier node = nodes[dir];
                if (node != null && node.get() != null && getNodeInflux(node) >= 0)
                {
                    components.set(dir, node);
                    ++outputs;
                }
            }

            for (int dir = 0; dir < getAdjVertices().length; ++dir)
            {
                PipeVertex vertex = getAdjacent(dir);
                if (vertex != null && vertex.getTotalHead() - this.getTotalHead() <= 0)
                {
                    components.set(dir, vertex);
                    ++outputs;
                }
            }

            final int[] ints = parent.getWorld().getRandom().ints(0, 6).distinct().limit(6).toArray();

            for (int dir : ints)
            {
                PipeFlowComponent component = components.get(dir);
                if (component == null) continue;

                long transferAmount = (long) Math.min(amount, Math.ceil(oldAmount / (float) outputs));
                long received = component.insert(dir, 0, transferAmount, (ServerWorld) parent.getWorld(), FluidVariant.of(Fluids.WATER), transaction);
                amount -= received;
            }

            transaction.commit();
        }
    }

    // Get the flow with respect to the node
    protected float getNodeInflux(NodeSupplier nodeSupplier)
    {
        float nodeFlow = nodeSupplier.get().getFlow();
        float otherFlow = getTotalHead() - getHead(nodeSupplier.getPos().face().ordinal());
        return Math.abs(nodeFlow) > Math.abs(otherFlow) ? nodeFlow : otherFlow;
    }

    @Override
    public void preTick()
    {
        long extracted;
        try (Transaction transaction = Transaction.openOuter())
        {
            for (int dir = 0; dir < nodes.length; ++dir)
            {
                if (nodes[dir] == null) continue;
                FluidNode node = nodes[dir].get();
                if (node == null) continue;

                Storage<FluidVariant> storage = node.getStorage((ServerWorld) parent.getWorld());

//                float dH = getTotalHead() + 0.5f - getHead(dir); // Negative for extraction, positive for insertion
                float f = getNodeInflux(nodes[dir]);
                long transferAmount = (long) Math.ceil(f * FluidConstants.BUCKET / 8);
                if (transferAmount < 0)
                {
                    extracted = storage.extract(FluidVariant.of(Fluids.WATER), -transferAmount, transaction);
                    amount += extracted;
                }
            }
            transaction.commit();
        }
        super.preTick();
    }

//    protected void transferVertex(int dir, List<PipeFlowComponent> components, TransactionContext transaction)
//    {
////        PipeVertex adj = getAdjVertex(dir);
////        if (adj == null || adj.getTotalHead() - this.getTotalHead() > 0) return;
//
//        // Determine which direction this vertex is connected to the adjacent one in. Argh.
//        for (int i = 0; i < adj.getAdjVertices().length; ++i)
//        {
//            if (adj.getAdjVertices()[i] != this) continue;
//
//            long transferAmount = Math.min(amount, oldAmount / components.size());
//            long received = adj.insert(dir, i, transferAmount, world, , transaction);
//            amount -= received;
//            break;
//        }
//
//    }

//    protected void transferNode(int dir, List<PipeFlowComponent> components, TransactionContext transaction)
//    {
//        if (nodes[dir] == null) return;
//        FluidNode node = nodes[dir].get();
//
//        long extracted;
//        if (transferAmount >= 0)
//        {
//            transferAmount = Math.min(oldAmount, transferAmount);
//            amount -= extracted;
//        }
//
//    }

    @Override
    public long[] getVelocity()
    {
        return velocity;
    }

    @Override
    public long insert(int fromDir, int toDir, long maxAmount, ServerWorld world, FluidVariant variant, TransactionContext transaction)
    {

        return super.insert(fromDir, toDir, maxAmount, world, variant, transaction);
    }

    @Override
    public boolean keepNetworkValid()
    {
        return numNodes() >= 2;
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
}
