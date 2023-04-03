package com.neep.neepmeat.transport.machine.fluid;

import com.neep.meatlib.api.event.InitialTicks;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class FluidPipeBlockEntity<T extends PipeVertex> extends BlockEntity
{
    public NbtCompound queuedNbt;
    protected PipeNetwork network;
    protected UUID networkUUID;
    protected final T vertex;
    protected final PipeConstructor<T> constructor;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state, PipeConstructor<T> constructor)
    {
        this(NMBlockEntities.FLUID_PIPE, pos, state, constructor);
    }

    public FluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PipeConstructor<T> constructor)
    {
        super(type, pos, state);
        this.vertex = constructor.create(this);
        this.constructor = constructor;
    }

    public void setNetwork(PipeNetwork network)
    {
        this.network = network;
        networkUUID = network != null ? network.getUUID() : null;
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (getWorld() instanceof ServerWorld serverWorld && world.getServer().isOnThread())
        {
            if (queuedNbt != null)
            {
                FluidNodeManager.getInstance(getWorld()).readNodes(getPos(), queuedNbt, serverWorld);

                InitialTicks.getInstance(serverWorld).queue(w ->
                {
                    // If networkUUID is not null, the block was saved with a network. If network is null, the network has not yet been loaded.
                    if (networkUUID != null && network == null ) PipeNetwork.createFromNbt(serverWorld, networkUUID);
                });
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        queuedNbt = nbt.copy();

        if (nbt.get("networkUUID") != null) networkUUID = nbt.getUuid("networkUUID");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt = FluidNodeManager.getInstance(getWorld()).writeNodes(getPos(), nbt);

        if (networkUUID != null) nbt.putUuid("networkUUID", networkUUID);
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();

        if (world instanceof ServerWorld serverWorld)
        {
            if (!serverWorld.isPosLoaded(pos.getX(), pos.getY()))
            {
                FluidNodeManager.getInstance(serverWorld).entityUnloaded(pos);
                PipeNetwork network = vertex.getNetwork();

                if (network != null && networkUUID != null)
                {
                    ((IServerWorld) serverWorld).getFluidNetworkManager().storeNetwork(network.getUUID(), network.toNbt());
                }
            }
            else
            {
                FluidNodeManager.getInstance(serverWorld).entityRemoved(pos);
            }
        }
    }

    public void update(PipeNetwork.UpdateReason reason)
    {
        if (network != null)
        {
            network.update(pos, vertex, reason);
        }
    }

    public boolean isCreatedDynamically()
    {
        return false;
    }

    public T getPipeVertex()
    {
        return vertex;
    }

    @FunctionalInterface
    public interface PipeConstructor<T extends PipeVertex>
    {
        T create(FluidPipeBlockEntity parent);
    }
}
