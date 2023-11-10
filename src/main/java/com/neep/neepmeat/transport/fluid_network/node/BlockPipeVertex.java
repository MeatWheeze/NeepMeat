package com.neep.neepmeat.transport.fluid_network.node;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.transport.api.pipe.FluidPipe;
import com.neep.neepmeat.transport.fluid_network.*;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class BlockPipeVertex extends SimplePipeVertex implements NbtSerialisable
{
    protected final FluidPipeBlockEntity<?> parent;
    protected final FluidNode[] nodes = new FluidNode[6];
    private final ObjectArrayList<PipeFlowComponent> components = new ObjectArrayList<>(6);
    protected boolean dirty;
    protected Random jrandom = new Random();

    protected Long[] queuedPositions = null;
    protected boolean[] queuedNodes = null;

    public BlockPipeVertex(FluidPipeBlockEntity<?> fluidPipeBlockEntity)
    {
        super(fluidPipeBlockEntity.getPos().asLong());
        this.parent = fluidPipeBlockEntity;
        setHeight(fluidPipeBlockEntity.getPos().getY());
        components.size(6);
    }

    @Override
    public boolean canSimplify()
    {
        return super.canSimplify() && numNodes() == 0;
    }

    @Override
    public void setAdjVertex(int dir, PipeVertex vertex)
    {
        super.setAdjVertex(dir, vertex);
        parent.markDirty();
    }

    public int numNodes()
    {
        int number = 0;
        for (var node : nodes)
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

    @Override
    public void updateNodes(ServerWorld world, BlockPos pos, BlockState state)
    {
        Arrays.fill(nodes, null);
        FluidPipe.findFluidPipe(world, pos, state).ifPresent(p ->
        {
            for (Direction direction : p.getConnections(state, d -> true))
            {
                FluidNode node = FluidNodeManager.getInstance(world).get(new NodePos(pos, direction));
                if (node != null)
                {
                    nodes[direction.ordinal()] = node;
                }
            }
        });
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
                FluidNode node = nodes[dir];
                if (node != null)
                {
                    if (getNodeFlow(node.getNodePos(), node) >= 0 && node.getMode().canInsert())
                    {
                        components.set(dir, node);
                        ++transfers;
                    }
                }
            }

            for (int dir = 0; dir < getAdjVertices().length; ++dir)
            {
                PipeVertex vertex = getAdjacent(dir);
                if (vertex != null && vertex.getTotalHeight() - this.getTotalHeight() <= 0)
                {
                    components.set(dir, vertex);
                    ++transfers;
                }
            }

            // Randomise transfer order to reduce opportunities for fluid to get stuck in loops.
            final int[] ints = jrandom.ints(0, 6).distinct().limit(6).toArray();

            for (int dir : ints)
            {
                PipeFlowComponent component = components.get(dir);
                if (component == null) continue;

                // Even if previous transfers failed, the remaining fluid should all be transferred.
                long transferAmount = (long) Math.min(amount, Math.ceil(amount / (float) transfers));

                // TODO: determine toDir so that check valves work again.
                // Apparently they have been broken since April 2023 without me noticing.
                long received = component.insert(dir, 0, transferAmount, (ServerWorld) parent.getWorld(), variant, transaction);

                if (received > 0)
                {
                    amount -= received;
                    if (amount <= 0)
                        variant = FluidVariant.blank();
                    dirty = true;
                }

                --transfers;
            }
            transaction.commit();

            if (dirty)
            {
                parent.markDirty();
                dirty = false;
            }
        }
    }

    // Get the flow with respect to the node
    protected float getNodeFlow(NodePos pos, FluidNode node)
    {
        // Negative for influx, positive for efflux.
        float nodeFlow = node.getFlow();

        // If a node is above this vertex, the height difference should be -0.5.
        float heightFlow = -(getHeight(pos.face().ordinal()) - height) - (node.getPressureHeight() - pumpHeight);

        return nodeFlow != 0 ? nodeFlow : heightFlow;
    }

    protected void stepHead()
    {
        float total = 0;
        int found = 1;

        total += getPumpHead();

        for (int dir = 0; dir < 6; ++dir)
        {
            PipeVertex vertex = getAdjacent(dir);
            FluidNode node = nodes[dir];

            if (vertex != null)
            {
                total += vertex.getPumpHead();
                found++;
            }
            else if (node != null)
            {
                total += node.getPressureHeight();
                found++;
            }
        }

        if (found == 0)
        {
            pumpHeight = 0;
        }
        else
        {
            float f = total / found;
            pumpHeight = Math.abs(f) <= 0.01 ? 0 : f;
        }
    }

    @Override
    public void preTick()
    {
        deferredLoad();

        stepHead();

        for (FluidNode node : nodes)
        {
            if (node == null) continue;

            node.setPressureHeight((pumpHeight) / 2);
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            for (FluidNode node : nodes)
            {
                if (node == null) continue;

                Storage<FluidVariant> storage = node.getStorage((ServerWorld) parent.getWorld());

                float f = getNodeFlow(node.getNodePos(), node);

                // Calculate the amount with respect to this vertex.
                long transferAmount = - (long) Math.ceil(f * getCapacity());
                if (transferAmount > 0)
                {
                    FluidVariant foundVariant = StorageUtil.findExtractableResource(storage, transaction);
                    long extracted;
                    if (foundVariant != null && (variant.isBlank() || foundVariant.equals(variant)))
                    {
                        // Note the negative signs
                        long permittedAmount = canInsert((ServerWorld) parent.getWorld(), node.getNodePos().face().getOpposite().ordinal(), foundVariant, transferAmount);
                        extracted = storage.extract(foundVariant, permittedAmount, transaction);
                        if (extracted > 0)
                        {
                            variant = foundVariant;
                            amount += extracted;
                            dirty = true;
                        }
                    }
                }
            }
            transaction.commit();

            if (dirty)
            {
                parent.markDirty();
                dirty = false;
            }
        }
        super.preTick();
    }

    @Override
    public void addHead(int h)
    {
        pumpHeight += h;

        for (FluidNode node : nodes)
        {
            if (node == null) continue;

            // Simulate an extra level of depth for each attached node.
            // If the effective height at this position is -14, all attached nodes will have an effective height of -13.
            node.setPressureHeight(pumpHeight - Math.signum(h) * 1);
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
    public float getPumpHead()
    {
        return pumpHeight;
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
        for (FluidNode node : nodes)
        {
            if (node != null) ++count;
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
        return "Vertex@" + System.identityHashCode(this) + "{connection=" + adj + "nodes: " + Arrays.toString(nodes) + ", head:" + getTotalHeight() + "}";
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
        nbt.putLong("amount", amount);
        nbt.put("variant", variant.toNbt());

        NbtList adjacent = new NbtList();
        for (int dir = 0; dir < 6; dir++)
        {
            NbtCompound adjNbt = new NbtCompound();
            adjacent.add(adjNbt);

            PipeVertex adj = getAdjVertex(dir);
            if (adj != null)
            {
                adjNbt.putLong("pos", adj.getPos());
            }

            if (nodes[dir] != null)
            {
                adjNbt.putBoolean("node", true);
            }
        }

        nbt.put("adjacent", adjacent);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.amount = nbt.getLong("amount");
        this.variant = FluidVariant.fromNbt(nbt.getCompound("variant"));

        NbtList adjacent = nbt.getList("adjacent", NbtElement.COMPOUND_TYPE);

        queuedPositions = new Long[6];
        queuedNodes = new boolean[6];
        for (int dir = 0; dir < 6; ++dir)
        {
            NbtCompound adjNbt = adjacent.getCompound(dir);

            if (adjNbt.contains("pos"))
            {
                queuedPositions[dir] = adjNbt.getLong("pos");
            }

            queuedNodes[dir] = adjNbt.contains("node");

        }
    }

    protected void deferredLoad()
    {
        if (queuedPositions != null)
        {
            for (int dir = 0; dir < 6; ++dir)
            {
                Long adjPos = queuedPositions[dir];

                if (adjPos == null)
                    continue;

                setAdjVertex(dir, PipeVertex.LOOKUP.find(parent.getWorld(), BlockPos.fromLong(adjPos), null));
            }

            queuedPositions = null;
        }

        if (queuedNodes != null)
        {
            for (int dir = 0; dir < 6; ++dir)
            {
                if (queuedNodes[dir])
                {
                    FluidNode node = FluidNodeManager.getInstance(parent.getWorld()).get(new NodePos(BlockPos.fromLong(pos), Direction.values()[dir]));
                    if (node != null)
                    {
                        nodes[dir] = node;
                    }
                }
            }
        }
    }
}
