package com.neep.neepmeat.transport.fluid_network.node;

import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.block.vat.ItemPortBlock;
import com.neep.neepmeat.transport.block.fluid_transport.IDirectionalFluidAcceptor;
import com.neep.neepmeat.transport.block.fluid_transport.IVariableFlowBlock;
import com.neep.neepmeat.transport.fluid_network.FluidNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

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
    private PipeNetwork network = null;
    public long networkId;
    public Map<FluidNode, Integer> distances = new HashMap<>();

    private FluidPump pump;
    private boolean hasPump;

    private Storage<FluidVariant> storage;
    public boolean isStorage;


    public boolean needsDeferredLoading;

    public FluidNode(NodePos nodePos, ServerWorld world)
    {
        this.face = nodePos.face;
        this.pos = nodePos.pos;
        this.isStorage = findStorage(world);
        this.hasPump = findPump(world);
        this.nodePos = nodePos;
    }

    public FluidNode(NodePos nodePos, Storage<FluidVariant> storage, boolean isStorage, boolean hasPump)
    {
        this(nodePos, storage, null);
        this.isStorage = isStorage;
        this.hasPump = hasPump;
    }

    public FluidNode(NodePos nodePos, Storage<FluidVariant> storage, @Nullable FluidPump pump)
    {
        this.pos = nodePos.pos;
        this.face = nodePos.face;
        this.nodePos = nodePos;
        this.storage = storage;
        this.pump = pump;
        this.isStorage = true;
    }

    // For deferred loading only.
    protected FluidNode(NodePos pos, long networkId, ServerWorld world, boolean isStorage, boolean hasPump)
    {
        this.face = pos.face;
        this.pos = pos.pos;
        this.nodePos = pos;
        this.networkId = networkId;
        this.storage = null;
        this.pump = null;
        this.isStorage = isStorage;
        this.hasPump = hasPump;
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
        boolean isStorage = nbt.getBoolean("is_storage");
        boolean hasPump = nbt.getBoolean("hasPump");

        return new FluidNode(pos, networkId, world, isStorage, hasPump);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("pos", nodePos.toNbt(new NbtCompound()));
        nbt.putLong("network_id", networkId);
        nbt.putBoolean("is_storage", isStorage);
        nbt.putBoolean("hasPump", hasPump);
        return nbt;
    }

    public void loadDeferred(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
        Optional<PipeNetwork> net = PipeNetwork.tryCreateNetwork(world, pos, Direction.NORTH);
    }

    private void load(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
        boolean bl1 = findStorage(world);
        boolean bl2 = findPump(world);
    }

    public boolean findPump(ServerWorld world)
    {
        FluidPump pump;
        if ((pump = FluidPump.SIDED.find(world, pos.offset(face), face.getOpposite())) != null)
        {
            this.pump = pump;
            return true;
        }
        return false;
    }

    public boolean findStorage(ServerWorld world)
    {
        Storage<FluidVariant> storage;
       if ((storage = FluidStorage.SIDED.find(world, pos.offset(face), face.getOpposite())) != null)
        {
            this.storage = storage;
            return true;
//            this.needsDeferredLoading = false;
        }
        return false;
            // Remove nodes with no connected storage that are not queued for deferred loading
//            FluidNetwork.getInstance(world).removeNode(world, nodePos);
    }

    public void setStorage(Storage<FluidVariant> storage)
    {
        this.storage = storage;
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
        if (hasPump) return pump.getFlow();
        return getMode(world).getFlow();
    }

    public static double exactDistance(FluidNode node1, FluidNode node2)
    {
        Vec3d offset1 = new Vec3d(node1.face.getUnitVector()).multiply(0.5);
        Vec3d v1 = Vec3d.ofCenter(node1.pos).add(offset1);
        Vec3d offset2 = new Vec3d(node2.face.getUnitVector()).multiply(0.5);
        Vec3d v2 = Vec3d.ofCenter(node2.pos).add(offset2);
        return NMMaths.manhattanDistance(v1, v2);
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
        long max = 0;
        for (StorageView<FluidVariant> view : getStorage(world).iterable(inner))
        {
            max = Math.max(max, view.getAmount());
        }
        inner.abort();
        return max;
    }

    public long firstCapacity(ServerWorld world, TransactionContext transaction)
    {
        Transaction inner = transaction.openNested();
        long max = 0;
        for (StorageView<FluidVariant> view : getStorage(world).iterable(inner))
        {
            max = Math.max(max, view.getCapacity());
        }
        inner.abort();
        return max;
    }

    public AcceptorModes getMode(ServerWorld world)
    {
        if (hasPump) pump.getMode();
        return AcceptorModes.INSERT_EXTRACT;
    }

    public boolean canInsert(ServerWorld world, TransactionContext transaction)
    {
        Storage<FluidVariant> storage;
        if (!(storage = getStorage(world)).supportsInsertion())
            return false;
        Transaction nested = transaction.openNested();
        Iterator<StorageView<FluidVariant>> it = (Iterator<StorageView<FluidVariant>>) storage.iterator(nested);
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
        Iterator<StorageView<FluidVariant>> it = (Iterator<StorageView<FluidVariant>>) storage.iterator(nested);
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
