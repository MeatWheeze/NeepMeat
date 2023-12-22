package com.neep.neepmeat.fluid_transfer.node;

import com.neep.neepmeat.block.fluid_transport.IDirectionalFluidAcceptor;
import com.neep.neepmeat.block.fluid_transport.IVariableFlowBlock;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.PipeNetwork;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.system.CallbackI;

import java.util.HashMap;
import java.util.Iterator;
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
    public float flowMultiplier;
    private PipeNetwork network = null;
    public long networkId;
    public Map<FluidNode, Integer> distances = new HashMap<>();
    private Storage<FluidVariant> storage;

    public boolean isStorage;

    public boolean needsDeferredLoading;

    public FluidNode(NodePos nodePos, Storage<FluidVariant> storage, float flowMultiplier, boolean isStorage)
    {
        this(nodePos, storage, flowMultiplier);
        this.isStorage = isStorage;
    }

    public FluidNode(NodePos nodePos, Storage<FluidVariant> storage, float flowMultiplier)
    {
        this.pos = nodePos.pos;
        this.face = nodePos.face;
        this.nodePos = nodePos;
        this.storage = storage;
        this.flowMultiplier = flowMultiplier;
        this.isStorage = true;
    }

    // For deferred loading only.
    protected FluidNode(NodePos pos, float flowMultiplier, long networkId, ServerWorld world, boolean isStorage)
    {
        this.face = pos.face;
        this.pos = pos.pos;
        this.nodePos = pos;
        this.flowMultiplier = flowMultiplier;
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

        return new FluidNode(pos, flowMultiplier, networkId, world, isStorage);
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

    // TODO: Find out what this does
    public void setMode(AcceptorModes mode)
    {
//        this.flow = mode.getFlow();
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

    public BlockPos getTargetPos()
    {
        return pos.offset(face);
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public NodePos getNodePos()
    {
        return this.nodePos;
    }

    public float getFlow(ServerWorld world)
    {
        BlockState state = world.getBlockState(getTargetPos());
        if (state.getBlock() instanceof IVariableFlowBlock variableFlow)
        {
            return variableFlow.getFlow(world, getTargetPos(), state);
        }
        return getMode(world).getFlow() * this.flowMultiplier;
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

    // Gets a quick and dirty idea of the tank's contents
    public long firstAmount(ServerWorld world, TransactionContext transaction)
    {
        Transaction inner = transaction.openNested();
        for (StorageView<FluidVariant> view : getStorage(world).iterable(inner))
        {
            inner.abort();
            return view.getAmount();
        }
        inner.abort();
        return 0;
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

    public boolean canInsert(ServerWorld world, TransactionContext transaction)
    {
        Storage<FluidVariant> storage;
        if (!(storage = getStorage(world)).supportsInsertion())
            return false;
        Transaction nested = transaction.openNested();
        Iterator<StorageView<FluidVariant>> it = storage.iterator(nested);
        while (it.hasNext())
        {
            StorageView<FluidVariant> view = it.next();
            if (view.getAmount() < view.getCapacity())
            {
                nested.abort();
                return true;
            }
        }
        nested.abort();
        return false;
    }

    public boolean canExtract(ServerWorld world, TransactionContext transaction)
    {
        Storage<FluidVariant> storage;
        if (!(storage = getStorage(world)).supportsExtraction())
            return false;

        Transaction nested = transaction.openNested();
        Iterator<StorageView<FluidVariant>> it = storage.iterator(nested);
        while (it.hasNext())
        {
            StorageView<FluidVariant> view = it.next();
            if (view.getAmount() > 0 && !view.isResourceBlank())
            {
                nested.abort();
                return true;
            }
        }
        nested.abort();
        return false;
    }
}
