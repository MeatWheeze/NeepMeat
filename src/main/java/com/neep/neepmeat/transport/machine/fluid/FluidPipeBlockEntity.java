package com.neep.neepmeat.transport.machine.fluid;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPipeBlockEntity<T extends PipeVertex> extends BlockEntity
{
    public NbtCompound queuedNbt;
    protected PipeNetwork network;
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
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (getWorld() instanceof ServerWorld serverWorld && world.getServer().isOnThread() && queuedNbt != null)
        {
            FluidNodeManager.getInstance(getWorld()).readNodes(getPos(), queuedNbt, serverWorld);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        queuedNbt = nbt.copy();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt = FluidNodeManager.getInstance(getWorld()).writeNodes(getPos(), nbt);
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();

        // Accessing FluidNodeManager on client must fail
        if (world.isClient()) return;

        if (!world.isPosLoaded(pos.getX(), pos.getY()))
        {
            FluidNodeManager.getInstance(world).entityUnloaded(pos);
        }
        else
        {
            FluidNodeManager.getInstance(world).entityRemoved(pos);
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
