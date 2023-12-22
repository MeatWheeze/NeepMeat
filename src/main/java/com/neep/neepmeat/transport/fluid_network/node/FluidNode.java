package com.neep.neepmeat.transport.fluid_network.node;

import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

/*
    An interface for fluid networks associated with a block and a direction
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidNode
{
    private final NodePos pos;
    private final NodePos nodePos;

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
    protected FluidNode(NodePos pos, ServerWorld world, boolean isStorage, boolean hasPump)
    {
        this.pos = pos;
        this.nodePos = pos;
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
        boolean isStorage = nbt.getBoolean("is_storage");
        boolean hasPump = nbt.getBoolean("hasPump");

        return new FluidNode(pos, world, isStorage, hasPump);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("pos", nodePos.toNbt(new NbtCompound()));
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
        boolean bl1 = findStorage(world);
        boolean bl2 = findPump(world);

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
        }
        return false;
    }

    public void setStorage(Storage<FluidVariant> storage)
    {
        this.storage = storage;
    }

    // Removes node from and revalidates the network
    public void onRemove()
    {
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

    public Storage<FluidVariant> getStorage(ServerWorld world)
    {
        if (storage == null)
        {
            load(world);
        }
        return storage;
    }

    public FluidPump getPump()
    {
        if (!hasPump) return null;
        return pump;
    }

    public AcceptorModes getMode()
    {
        if (hasPump)
        {
            return getPump().getMode();
        }
        return AcceptorModes.INSERT_EXTRACT;
    }

    // This section is another level of jank to add to the already numerous strata.
    protected float pressureHeight;

    public void setPressureHeight(float height)
    {
        this.pressureHeight = height;
    }

    public float getPressureHeight()
    {
        return pressureHeight;
    }
}