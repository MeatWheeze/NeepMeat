package com.neep.neepmeat.fluid_transfer.node;

import com.neep.neepmeat.block.IDirectionalFluidAcceptor;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.PipeNetwork;
import com.neep.neepmeat.util.FilterUtils;
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
    private PipeNetwork network = null;
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
        Optional<PipeNetwork> net = PipeNetwork.tryCreateNetwork(world, pos, Direction.NORTH);
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

    public void setNetwork(ServerWorld world, PipeNetwork network)
    {
//        load(world);
        setNetwork(network);
    }

    public void setNetwork(PipeNetwork network)
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

    public PipeNetwork getNetwork()
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
                || node.getMode(world) == AcceptorModes.NONE || node.getMode(world) == AcceptorModes.PUSH
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

        // TODO: Fix getFlow()
        float Q = this.getMode(world).getFlow() * this.flowMultiplier -
                node.getMode(world).getFlow() * node.flowMultiplier;

        long maxFlow = 10500;

        Transaction t1 = Transaction.openOuter();
        long moved = StorageUtil.move(getStorage(world), node.getStorage(world), FilterUtils::any, Long.MAX_VALUE, t1);
        t1.abort();

//        long baseFlow = Math.min(maxFlow, this.getStorage(world).simulateExtract(NMFluids.UNCHARGED, maxFlow, null));
//        long baseFlow = Math.min(maxFlow, moved);
        long baseFlow = moved;
//        float insertBranchFlow = (float) (((float) baseFlow * (Q + gravityFlowIn)) / distances.size());
        long insertBranchFlow = (long) (baseFlow * (Q + Math.ceil(gravityFlowIn)) / (distances.size()));
        System.out.println(Math.ceil(gravityFlowIn));
//        float insertBranchFlow = (float) (baseFlow * (Q + gravityFlowIn) * (float) ((Math.pow(r, 4) / (distances.get(node))) / sumIn));
//        float extractBranchFlow = (float) (baseFlow * (Q + gravityFlowIn) * (float) ((Math.pow(r, 4) / (distances.get(node))) / sumOut));

        long amountMoved = 0;
        Transaction transaction = Transaction.openOuter();
        if (insertBranchFlow >= 0)
        {
            amountMoved = StorageUtil.move(getStorage(world), node.getStorage(world), FilterUtils::any, insertBranchFlow, transaction);
//            if (amountMoved == (long) insertBranchFlow)
//            {
//                transaction.commit();
//            }
//            else
//            {
//                transaction.abort();
//            }
        }
        transaction.commit();
//        if (false)
//        {
//            amountMoved = StorageUtil.move(node.getStorage(world), getStorage(world), variant -> true, (long) - extractBranchFlow, transaction);
//            if (amountMoved == (long) - extractBranchFlow)
//            {
//                transaction.commit();
//            }
//            else
//            {
//                transaction.abort();
//            }
//        }
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
