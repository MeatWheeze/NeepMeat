package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_util.FluidNetwork;
import com.neep.neepmeat.init.BlockEntityInitialiser;
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
        super(BlockEntityInitialiser.NODE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
//        if (getWorld() instanceof ServerWorld serverWorld && queuedNbt != null)
//        {
//            FluidNetwork.INSTANCE.readNodes(serverWorld, getPos(), queuedNbt);
//        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        queuedNbt = nbt.copy();
        FluidNetwork.INSTANCE.readNodes(getPos(), nbt);

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt = FluidNetwork.INSTANCE.writeNodes(getPos(), nbt);
        return nbt;
    }
}
