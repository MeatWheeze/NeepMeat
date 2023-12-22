package com.neep.neepmeat.blockentity.integrator;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IntegratorEggBlockEntity extends BlockEntity implements
        BlockEntityClientSerializable
{
    protected int growthTimeRemaining = 1000;

    public IntegratorEggBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.INTEGRATOR_EGG, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return writeNbt(tag);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, IntegratorEggBlockEntity be)
    {
        if (be.canGrow())
        {
            --be.growthTimeRemaining;
        }
    }

    public boolean canGrow()
    {
        return growthTimeRemaining > 0;
    }
}
