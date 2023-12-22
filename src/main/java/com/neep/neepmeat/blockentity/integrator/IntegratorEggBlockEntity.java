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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

@SuppressWarnings("UnstableApiUsage")
public class IntegratorEggBlockEntity extends BlockEntity implements
        FluidBufferProvider,
        BlockEntityClientSerializable, IAnimatable
{

    protected int growthTimeRemaining = 1000;
    protected final TypedFluidBuffer buffer;
    int totalTime = 10;

    public IntegratorEggBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.INTEGRATOR_EGG, pos, state);
        buffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.getFluid() instanceof BloodFluid);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt("growth_remaining", growthTimeRemaining);
        tag = buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        growthTimeRemaining = tag.getInt("growth_remaining");
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
        be.grow();
    }


    public boolean canGrow()
    {
        return growthTimeRemaining > 0 && buffer.getAmount() >= FluidConstants.BUCKET / totalTime / 20;
    }

    public void grow()
    {
        if (!canGrow())
            return;

        long decrement = FluidConstants.BUCKET / totalTime / 20;
        Transaction transaction = Transaction.openOuter();
        long transferred = buffer.extract(buffer.getResource(), decrement, transaction);
        if (transferred == decrement)
        {
            --growthTimeRemaining;
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

    @Override
    public void registerControllers(AnimationData animationData)
    {

    }

    @Override
    public AnimationFactory getFactory()
    {
        return null;
    }
}
