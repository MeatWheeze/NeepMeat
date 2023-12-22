package com.neep.neepmeat.transport.machine.fluid;

import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
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
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockPipeVertex extends SimplePipeVertex
{
    private final FluidPipeBlockEntity parent;
    protected final NodeSupplier[] nodes = new NodeSupplier[6];
    protected long[] newAmounts = new long[6];
    protected long[] amounts = new long[6];

    protected Vec3d newVelocity;
    protected Vec3d velocity;

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

    @Override
    public void tick()
    {


        int dir;
        try (Transaction transaction = Transaction.openOuter())
        {
            for (dir = 0; dir < nodes.length; ++dir)
            {
                if (nodes[dir] == null) continue;
                FluidNode node = nodes[dir].get();

                Storage<FluidVariant> storage = node.getStorage((ServerWorld) parent.getWorld());
                float dH = getTotalHead() + 0.5f - getHead(dir); // Negative for extraction, positive for insertion
                long transferAmount = (long) (dH * FluidConstants.BUCKET / 8);
                long extracted;
                if (transferAmount < 0)
                {
                    extracted = storage.extract(FluidVariant.of(Fluids.WATER), -transferAmount, transaction);
                    amount += extracted;
                }
                else
                {
                    transferAmount = Math.min(amount, transferAmount);
                    extracted = storage.insert(FluidVariant.of(Fluids.WATER), transferAmount, transaction);
                    amount -= extracted;
                }
                transaction.commit();
            }

            List<PipeVertex> toTransfer = Arrays.stream(getAdjVertices()).filter(v -> v != null && v.getTotalHead() - this.getTotalHead() <= 0).collect(Collectors.toList());
            for (PipeVertex adj : toTransfer)
            {
                // Determine which direction this vertex is connected to the adjacent one in. Argh.
                for (int i = 0; i < adj.getAdjVertices().length; ++i)
                {
                    if (adj.getAdjVertices()[i] != this) continue;

                    long transferAmount = amount / toTransfer.size();
                    long received = adj.insert(dir, i, transferAmount, transaction);
                    amount -= received;
                    break;
                }
            }
        }
    }

    @Override
    public long insert(int fromDir, int toDir, long maxAmount, TransactionContext transaction)
    {

        return super.insert(fromDir, toDir, maxAmount, transaction);
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
