package com.neep.neepmeat.blockentity.integrator;

import com.neep.neepmeat.blockentity.FluidBufferProvider;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid_util.TypedFluidBuffer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
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
        tag = buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNBT(tag);
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
//        be.grow();
    }

    int totalTime = 10;

    public boolean canGrow()
    {
        return growthTimeRemaining > 0;
    }

    public void grow()
    {
        if (!canGrow())
            return;

        --growthTimeRemaining;
        long decrement = FluidConstants.BUCKET / totalTime / 20;
        Transaction transaction = Transaction.openOuter();
        long transferred = buffer.extract(buffer.getResource(), decrement, transaction);
        if (transferred == decrement)
        {
            transaction.commit();
        }
        else
        {
            transaction.abort();
        }
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
