package com.neep.neepmeat.transport.machine.fluid;

import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockPipeVertex extends SimplePipeVertex
{
    private final FluidPipeBlockEntity parent;
    protected final NodeSupplier[] nodes = new NodeSupplier[6];
    protected long[] newAmounts = new long[6];
    protected long[] amounts = new long[6];
    protected long[] newVelocity = new long[6];
    protected long[] velocity = new long[6];

//    protected Vec3d newVelocity;
//    protected Vec3d velocity;

    public BlockPipeVertex(FluidPipeBlockEntity fluidPipeBlockEntity)
    {
        this.parent = fluidPipeBlockEntity;
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
            case 0 -> elevationHead + pressureHead;
            case 1 -> elevationHead + pressureHead + 1;
            default -> elevationHead + pressureHead + 0.5f;
        };
    }

    private final List<PipeFlowComponent> components = new ObjectArrayList<>(6);
    private final List<Integer> directions = new IntArrayList(6);

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


        int dir;
        try (Transaction transaction = Transaction.openOuter())
        {
            components.clear();
            for (dir = 0; dir < nodes.length; ++dir)
            {
                NodeSupplier node = nodes[dir];
                if (node != null && node.get() != null && getNodeEfflux(node) <= 0)
                {
                    components.add(dir, node);
                }
            }

            for (dir = 0; dir < getAdjVertices().length; ++dir)
            {
                PipeVertex vertex = getAdjacent(dir);
                if (vertex != null && vertex.getTotalHead() - this.getTotalHead() < 0)
                {
                    components.add(dir, vertex);
                }
            }

            for (dir = 0; dir < components.size(); ++dir)
            {
                PipeFlowComponent component = components.get(dir);
                if (component == null) continue;

                long transferAmount = Math.min(amount, oldAmount / components.size());
                long received = component.insert(dir, 0, transferAmount, (ServerWorld) parent.getWorld(), FluidVariant.of(Fluids.WATER), transaction);
                amount -= received;
            }

            transaction.commit();
        }
    }

    // Get the flow with respect to the node
    protected float getNodeEfflux(NodeSupplier nodeSupplier)
    {
        return getTotalHead() - nodeSupplier.get().getFlow();
    }

    @Override
    public void preTick()
    {
        long extracted;
        try (Transaction transaction = Transaction.openOuter())
        {
            for (int dir = 0; dir < nodes.length; ++dir)
            {
                if (nodes[dir] == null) return;
                FluidNode node = nodes[dir].get();
                Storage<FluidVariant> storage = node.getStorage((ServerWorld) parent.getWorld());

                float dH = getTotalHead() + 0.5f - getHead(dir); // Negative for extraction, positive for insertion
                long transferAmount = (long) (dH * FluidConstants.BUCKET / 8);
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
    public String toString()
    {
        StringBuilder adj = new StringBuilder();
        for (PipeVertex v : getAdjVertices()) {
            if (v != null) adj.append(System.identityHashCode(v)).append(", ");
        }
        return "Vertex@" + System.identityHashCode(this) + "{connection=" + adj + "nodes: " + Arrays.toString(nodes) + ", head:" + getTotalHead() + "}";
    }
}
