package com.neep.neepmeat.fluid_transfer.node;

import com.neep.neepmeat.block.IDirectionalFluidAcceptor;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.NMFluidNetwork;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
    An interface for fluid networks associated with a block and a direction
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidNode
{
    private final Direction face;
    private final BlockPos pos;
    private final NodePos nodePos;
    public float flow;
    public float flowMultiplier;
    private NMFluidNetwork network = null;
    public long networkId;
    public Map<FluidNode, Integer> distances = new HashMap<>();
    private Storage<FluidVariant> storage;

    private boolean canInsert;
    private boolean canExtract;
    public boolean isStorage;

    public boolean needsDeferredLoading;

    public FluidNode(NodePos nodePos, Storage<FluidVariant> storage, AcceptorModes mode, float flowMultiplier, boolean isStorage)
    {
        this(nodePos, storage, mode, flowMultiplier);
        this.isStorage = isStorage;
    }

    public FluidNode(NodePos nodePos, Storage<FluidVariant> storage, AcceptorModes mode, float flowMultiplier)
    {
        this.pos = nodePos.pos;
        this.face = nodePos.face;
        this.nodePos = nodePos;
        this.storage = storage;
        this.flowMultiplier = flowMultiplier;
        this.flow = mode.getFlow() * flowMultiplier;
        this.isStorage = true;
    }

    // For deferred loading only.
    protected FluidNode(NodePos pos, AcceptorModes mode, float flowMultiplier, long networkId, ServerWorld world, boolean isStorage)
    {
        this.face = pos.face;
        this.pos = pos.pos;
        this.nodePos = pos;
        this.flowMultiplier = flowMultiplier;
        this.flow = mode.getFlow() * flowMultiplier;
        this.networkId = networkId;
        this.storage = null;
        this.isStorage = isStorage;
        this.needsDeferredLoading = true;

        FluidNetwork.getInstance(world).queueNode(this);
    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + face + " storage: " + storage;
    }

    // Load a node from NBT data
    public static FluidNode fromNbt(NbtCompound nbt, ServerWorld world)
    {
        NbtCompound posNbt = nbt.getCompound("pos");
        NodePos pos = NodePos.fromNbt(posNbt);
        AcceptorModes mode = AcceptorModes.byId(nbt.getInt("mode"));
        long networkId = nbt.getLong("network_id");
        float flowMultiplier = nbt.getFloat("multiplier");
        boolean isStorage = nbt.getBoolean("is_storage");

        return new FluidNode(pos, mode, flowMultiplier, networkId, world, isStorage);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("pos", nodePos.toNbt(new NbtCompound()));
        nbt.putLong("network_id", networkId);
        nbt.putFloat("multiplier", flowMultiplier);
        nbt.putBoolean("is_storage", isStorage);
        return nbt;
    }

    public int getDistance(FluidNode node)
    {
        Integer dist = distances.get(node);
        return dist != null ? dist : Integer.MAX_VALUE;
    }

    public void loadDeferred(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
//        load(world);
        Optional<NMFluidNetwork> net = NMFluidNetwork.tryCreateNetwork(world, pos, Direction.NORTH);
    }

    private void load(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
        findStorage(world);
    }

    public void findStorage(ServerWorld world)
    {
        Storage<FluidVariant> storage;
        if ((storage = FluidStorage.SIDED.find(world, pos.offset(face), face.getOpposite())) != null)
        {
            this.storage = storage;
            this.needsDeferredLoading = false;
        }
        else
        {
            // Remove nodes with no connected storage that are not queued for deferred loading
//            FluidNetwork.getInstance(world).removeNode(world, nodePos);
        }
    }

    public void setStorage(Storage<FluidVariant> storage)
    {
        this.storage = storage;
    }

    public void setMode(AcceptorModes mode)
    {
        this.flow = mode.getFlow();
    }

    public void setNetwork(ServerWorld world, NMFluidNetwork network)
    {
//        load(world);
        setNetwork(network);
    }

    public void setNetwork(NMFluidNetwork network)
    {
        if (!(this.network == null) && !this.network.equals(network))
        {
            this.network.removeNode(new NodePos(pos, face));
        }
        if (this.network != null)
        {
            System.out.println(network.uid + " replaces " + this.network.uid);
        }
        this.network = network;
        distances.clear();
    }

    public NMFluidNetwork getNetwork()
    {
        return network;
    }

    // Removes node from and revalidates the network
    public void onRemove()
    {
        if (!(this.network == null))
        {
            network.removeNode(new NodePos(pos, face));
            network = null;
        }
        distances.clear();
    }

    public Direction getFace()
    {
        return face;
    }

    public int getTargetY()
    {
        return pos.offset(face).getY();
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public NodePos getNodePos()
    {
        return this.nodePos;
    }

    public float getFlow()
    {
        return flow;
    }

    public Storage<FluidVariant> getStorage(ServerWorld world)
    {
        if (!world.getServer().isOnThread() || !isStorage)
        {
            return null;
        }
        if (storage == null)
        {
            load(world);
        }
        return storage;
    }

    public AcceptorModes getMode(ServerWorld world)
    {
        BlockPos target = nodePos.facingBlock();
        BlockState state = world.getBlockState(target);
        if (state.getBlock() instanceof IDirectionalFluidAcceptor acceptor)
        {
            return acceptor.getDirectionMode(world, target, state, face.getOpposite());
        }
        return AcceptorModes.INSERT_EXTRACT;
    }

    public void transmitFluid(ServerWorld world, FluidNode node)
    {
//        System.out.println(node.getNodePos().facingBlock() + ", " + node.getMode(world));
        if (distances.get(node) == null
                || node.getMode(world) == AcceptorModes.NONE
                || this.getMode(world) == AcceptorModes.NONE
                || !node.isStorage
                || !this.isStorage
                )
        {
            return;
        }

        AcceptorModes mode = this.getMode(world);
        float flow = mode.getFlow() * flowMultiplier;

        // Here is the most realistic flow rate calculation that you have ever seen.

        float h = getTargetY() - node.getTargetY();
        double gravityFlowIn = h < -1 ? 0 : 0.1 * h;
//        double gravityFlowIn = 0;

        // Geometrical solution for velocity in branched pipes:
        // https://physics.stackexchange.com/questions/31852/flow-of-liquid-among-branches

        float r = 0.5f;

        float sumIn = 0;
        float sumOut = 0;

        // Calculate sum(r^4 / L_i), discounting full containers.
        for (FluidNode distanceNode : distances.keySet())
        {
            if (distanceNode.getStorage(world) == null)
            {
                continue;
            }

            Transaction transaction = Transaction.openOuter();

            boolean notFull = false;
            boolean notEmpty = false;

            for (StorageView<FluidVariant> view : this.getStorage(world).iterable(transaction))
            {
                for (StorageView<FluidVariant> targetView : distanceNode.getStorage(world).iterable(transaction))
                {
                    if (targetView.getAmount() < targetView.getCapacity() && (targetView.getResource().equals(view.getResource()) || targetView.isResourceBlank()) || targetView.getAmount() <= 0)
                    {
                        notFull = true;
                    }
                    if (targetView.getAmount() > 0 && (targetView.getResource().equals(view.getResource()) || view.isResourceBlank()))
                    {
                        notEmpty = true;
                    }
                }
            }
            transaction.abort();

            if (notFull)
            {
                sumIn += Math.pow(r, 4) / (float) distances.get(distanceNode);
            }
            if (notEmpty)
            {
                sumOut += Math.pow(r, 4) / (float) distances.get(distanceNode);
            }
        }

        float Q = this.getFlow() + node.getFlow();

        float insertBranchFlow = (float) (10500 * (flow + gravityFlowIn) * (float) ((Math.pow(r, 4) / (distances.get(node))) / sumIn));
        float extractBranchFlow = (float) (10500 * (flow + gravityFlowIn) * (float) ((Math.pow(r, 4) / (distances.get(node))) / sumOut));

        long amountMoved;
        if (insertBranchFlow >= 0)
        {
            amountMoved = StorageUtil.move(getStorage(world), node.getStorage(world), variant -> true, (long) insertBranchFlow, null);
        }
        else
        {
            amountMoved = StorageUtil.move(node.getStorage(world), getStorage(world), variant -> true, (long) - extractBranchFlow, null);
        }
//        System.out.println(amountMoved + node.toString());
    }

    public boolean canInsert()
    {
        return canInsert;
    }

    public void setCanInsert(boolean canInsert)
    {
        this.canInsert = canInsert;
    }

    public boolean canExtract()
    {
        return canExtract;
    }

    public void setCanExtract(boolean canExtract)
    {
        this.canExtract = canExtract;
    }
}
