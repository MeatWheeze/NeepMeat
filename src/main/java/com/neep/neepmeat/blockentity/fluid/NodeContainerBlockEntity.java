package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeContainerBlockEntity extends BlockEntity
{
    public NbtCompound queuedNbt;

    public NodeContainerBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.NODE_BLOCK_ENTITY, pos, state);
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
        FluidNodeManager.getInstance(world).markEntityRemoved(pos);
    }
}
