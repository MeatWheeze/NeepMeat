package com.neep.neepmeat.transport.fluid_network.node;

import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetworkImpl1;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.UUID;

/*
    An interface for fluid networks associated with a block and a direction
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidNode
{
    private final NodePos pos;
    private final NodePos nodePos;
    private PipeNetworkImpl1 network = null;
    protected boolean hasNetwork;
    protected UUID networkUUID;

    private FluidPump pump;
    private boolean hasPump;

    private Storage<FluidVariant> storage;
    public boolean isStorage;


    public boolean needsDeferredLoading;

    public FluidNode(NodePos nodePos, ServerWorld world)
    {
        this.pos = nodePos;
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
        this.pos = nodePos;
        this.nodePos = nodePos;
        this.storage = storage;
        this.pump = pump;
        this.isStorage = true;
    }

    // For deferred loading only.
    protected FluidNode(NodePos pos, UUID networkUUID, ServerWorld world, boolean isStorage, boolean hasPump)
    {
        this.pos = pos;
        this.nodePos = pos;
        this.networkUUID = networkUUID;
        this.storage = null;
        this.pump = null;
        this.isStorage = isStorage;
        this.hasPump = hasPump;
        this.needsDeferredLoading = true;

        FluidNodeManager.getInstance(world).queueNode(this);
    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + pos.face() + " storage: " + storage;
    }

    // Load a node from NBT data
    public static FluidNode fromNbt(NbtCompound nbt, ServerWorld world)
    {
        NbtCompound posNbt = nbt.getCompound("pos");
        NodePos pos = NodePos.fromNbt(posNbt);
        AcceptorModes mode = AcceptorModes.byId(nbt.getInt("mode"));
        UUID networkId = null;
        if (nbt.get("networkId") != null)
        {
            networkId = nbt.getUuid("networkId");
        }
        boolean isStorage = nbt.getBoolean("is_storage");
        boolean hasPump = nbt.getBoolean("hasPump");
        boolean hasNetwork = nbt.getBoolean("hasNetwork");

        return new FluidNode(pos, networkId, world, isStorage, hasPump);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("pos", nodePos.toNbt(new NbtCompound()));
        if (networkUUID != null)
        {
            nbt.putUuid("networkId", networkUUID);
        }
        nbt.putBoolean("is_storage", isStorage);
        nbt.putBoolean("hasPump", hasPump);
        nbt.putBoolean("hasNetwork", hasNetwork);
        return nbt;
    }

    public void loadDeferred(ServerWorld world)
    {
        if (!(needsDeferredLoading && storage == null) || !world.getServer().isOnThread())
        {
            return;
        }
        boolean bl1 = findStorage(world);
        boolean bl2 = findPump(world);

        // UUID will not be null if the node should be connected to a network. If network is null, the network must be
        // generated from NBT.
        if (networkUUID != null && network == null)
        {
            NbtCompound netNbt = ((IServerWorld) world).getFluidNetworkManager().getNetwork(networkUUID);
            if (netNbt != null)
            {
//                Optional<PipeNetworkImpl1> net = PipeNetworkImpl1.createFromNbt(world, netNbt);
            }
        }
        needsDeferredLoading = false;
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
        if ((pump = FluidPump.SIDED.find(world, pos.facingBlock(), pos.face().getOpposite())) != null)
        {
            this.pump = pump;
            return true;
        }
        return false;
    }

    public boolean findStorage(ServerWorld world)
    {
        Storage<FluidVariant> storage;
        BlockEntity be = world.getBlockEntity(pos.facingBlock());
        if ((storage = FluidStorage.SIDED.find(world, pos.facingBlock(), null, be, pos.face().getOpposite())) != null)
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

    public boolean isDriven()
    {
        return getFlow() != 0;
    }

    // Removes node from and revalidates the network
    public void onRemove()
    {
        if (!(this.network == null))
        {
//            network.removeNode(new NodePos(pos, face));
            setNetwork(null, false);
        }
    }

    public void setNetwork(PipeNetworkImpl1 network, boolean clearing)
    {
        if (this.network != null && this.network.equals(network)) return;

        // Skip removing this from the network if the current network is responsible for clearing this network...
        // Not sure how to explain it. It should prevent a ConcurrentModificationException.
        if (this.network != null && !clearing)
        {
            this.network.removeNode(pos);
        }

        this.network = network;
        this.networkUUID = network != null ? network.uuid : null;
    }

    public PipeNetworkImpl1 getNetwork()
    {
        return network;
    }

    public Direction getFace()
    {
        return pos.face();
    }

    public int getTargetY()
    {
        return pos.facingBlock().getY();
    }

    public NodePos getNodePos()
    {
        return this.nodePos;
    }

    public float getFlow()
    {
        if (hasPump) return getPump().getFlow();
        return getMode().getFlow();
    }

    public static double exactDistance(FluidNode node1, FluidNode node2)
    {
        Vec3d offset1 = new Vec3d(node1.pos.face().getUnitVector()).multiply(0.5);
        Vec3d v1 = Vec3d.ofCenter(node1.pos.pos()).add(offset1);
        Vec3d offset2 = new Vec3d(node2.pos.face().getUnitVector()).multiply(0.5);
        Vec3d v2 = Vec3d.ofCenter(node2.pos.pos()).add(offset2);
        return NMMaths.manhattanDistance(v1, v2);
    }

    public Storage<FluidVariant> getStorage(ServerWorld world)
    {
//        if (!world.getServer().isOnThread() || !isStorage)
//        {
//            return null;
//        }
        if (storage == null)
        {
            load(world);
        }
        return storage;
    }

    public FluidPump getPump()
    {
        if (!hasPump) return null;
        if (pump == null) findPump(network.getWorld());
        return pump;
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

    public AcceptorModes getMode()
    {
        if (hasPump)
        {
            return getPump().getMode();
        }
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
