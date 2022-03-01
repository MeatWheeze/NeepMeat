package com.neep.neepmeat.blockentity.integrator;

import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.fluid_util.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.fluid_util.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.BlockInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class IntegratorBlockEntity extends BlockEntity implements
        FluidBuffer.FluidBufferProvider, BlockEntityClientSerializable, IAnimatable
{

    protected int growthTimeRemaining = 1000;
    protected final MultiTypedFluidBuffer buffer;
    int totalTime = 10;
    public boolean isFullyGrown = false;
    protected TypedFluidBuffer inputBuffer;
    protected TypedFluidBuffer outputBuffer;

    public IntegratorBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.INTEGRATOR, pos, state);
        inputBuffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.getFluid() instanceof BloodFluid);
        outputBuffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> true);
        buffer = new MultiTypedFluidBuffer(this, List.of(inputBuffer, outputBuffer));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt("growth_remaining", growthTimeRemaining);
        tag.putBoolean("fully_grown", isFullyGrown);
        tag = buffer.writeNbt(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        growthTimeRemaining = tag.getInt("growth_remaining");
        isFullyGrown = tag.getBoolean("fully_grown");
        buffer.readNbt(tag);
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

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, IntegratorBlockEntity be)
    {
        if (be.canGrow())
        {
            be.grow();
        }
        if (be.isFullyGrown)
        {
            be.process();
        }
    }

    @Override
    public void registerControllers(AnimationData animationData)
    {

    }

    @Override
    public AnimationFactory getFactory()
    {
        return null;
    }

    public TypedFluidBuffer getInputBuffer()
    {
        return inputBuffer;
    }

    public TypedFluidBuffer getOutputBuffer()
    {
        return outputBuffer;
    }

    public boolean canGrow()
    {
        return growthTimeRemaining > 0 && inputBuffer.getAmount() >= FluidConstants.BUCKET / totalTime / 20;
    }

    public void grow()
    {
        long decrement = FluidConstants.BUCKET / totalTime / 20;
        Transaction transaction = Transaction.openOuter();
        long transferred = buffer.extract(inputBuffer.getResource(), decrement, transaction);
        if (transferred == decrement)
        {
            --growthTimeRemaining;
            transaction.commit();
        }
        else
        {
            transaction.abort();
        }

        if (growthTimeRemaining <= 0)
        {
            isFullyGrown = true;
        }
    }

    public void process()
    {
        long conversionAmount = 900;
        Transaction transaction = Transaction.openOuter();
        if (outputBuffer.getCapacity() - outputBuffer.getAmount() >= conversionAmount)
        {
            long extracted = inputBuffer.extract(FluidVariant.of(BlockInitialiser.STILL_BLOOD), conversionAmount, transaction);
            long inserted = outputBuffer.insert(FluidVariant.of(Fluids.WATER), extracted, transaction);
        }
        transaction.commit();
    }

    @Override
    public Storage<FluidVariant> getBuffer(Direction direction)
    {
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {

    }
}
