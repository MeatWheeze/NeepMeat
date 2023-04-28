package com.neep.neepmeat.transport.machine.fluid;

import com.neep.meatlib.api.event.InitialTicks;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
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

//    public PipeVertex.SaveState state = PipeVertex.SaveState.NEW;

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
        markDirty();
    }

    @Override
    public void markDirty()
    {
        world.markDirty(pos);
    }

    @Override
    public void setCachedState(BlockState state)
    {
        markReplaced();
        super.setCachedState(state);
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

            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        queuedNbt = nbt.copy();
//        this.state = PipeVertex.SaveState.values()[nbt.getInt("state")];

        if (nbt.get("networkUUID") != null) networkUUID = nbt.getUuid("networkUUID");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt = FluidNodeManager.getInstance(getWorld()).writeNodes(getPos(), nbt);

//        boolean l = world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));

        if (networkUUID != null && network != null)
        {
//            nbt.putInt("state", PipeVertex.SaveState.PENDING_LOAD.ordinal());
            nbt.putUuid("networkUUID", networkUUID);
        }
        else
        {
//            nbt.putInt("state", state.ordinal());
        }
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();

        if (world instanceof ServerWorld serverWorld)
        {
            if (replaced)
            {
//                FluidNodeManager.getInstance(serverWorld).entityRemoved(pos);
            }
        }
    }

    protected boolean replaced;

    public void markReplaced()
    {
        replaced = true;
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

//    public void setSaveState(PipeVertex.SaveState saveState)
//    {
//        this.state = saveState;
//        markDirty();
//    }

    public void onLoad(ServerWorld world)
    {
        // If the pipe had an attached network when it was saved
        if (network == null && networkUUID != null)
        {
            // Load the attached network from NBT
            PipeNetwork.retrieveNetwork(world, networkUUID);
        }
    }

    public void onUnload(ServerWorld world)
    {
        FluidNodeManager.getInstance(world).entityUnloaded(getPos());

        if (network != null)
        {
            // Hopefully allow the network and all attached block entities to be garbage collected
            PipeNetwork.stopTickingNetwork(network);
        }
    }

    public void onRemove(ServerWorld world)
    {
        FluidNodeManager.getInstance(world).entityRemoved(getPos());
    }

    @FunctionalInterface
    public interface PipeConstructor<T extends PipeVertex>
    {
        T create(FluidPipeBlockEntity<T> parent);
    }

    static
    {
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) ->
        {
            if (blockEntity instanceof FluidPipeBlockEntity<?> be)
            {
                if (!be.replaced)
                {
                    be.onUnload(world);
                }
                else
                {
                    be.onRemove(world);
                }
            }
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) ->
        {
            if (blockEntity instanceof FluidPipeBlockEntity<?> be)
            {
                InitialTicks.getInstance(world).queue(w ->
                {
                    if (!be.replaced)
                    {
                        be.onLoad(world);
                    }
                    else
                    {
                        be.replaced = false;
                    }
                });
            }
        });
    }
}
