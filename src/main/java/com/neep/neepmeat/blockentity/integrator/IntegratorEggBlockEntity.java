package com.neep.neepmeat.blockentity.integrator;

import com.neep.neepmeat.blockentity.FluidBufferProvider;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid_util.TypedFluidBuffer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.BlockInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class IntegratorEggBlockEntity extends BlockEntity implements
        FluidBufferProvider,
        BlockEntityClientSerializable
{

    protected int growthTimeRemaining = 1000;
    protected final TypedFluidBuffer buffer;

    public IntegratorEggBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.INTEGRATOR_EGG, pos, state);
        buffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.getFluid() instanceof BloodFluid);
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

    @Override
    public FluidBuffer getBuffer(Direction direction)
    {
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {

    }
}
