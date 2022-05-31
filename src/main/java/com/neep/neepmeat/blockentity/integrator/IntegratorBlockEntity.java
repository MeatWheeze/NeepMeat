package com.neep.neepmeat.blockentity.integrator;

import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
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
    public float facing = 0f;
    public float targetFacing = 0f;

    private final AnimationFactory factory = new AnimationFactory(this);

    public IntegratorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.INTEGRATOR, pos, state);
        inputBuffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(NMFluids.STILL_BLOOD), TypedFluidBuffer.Mode.INSERT_ONLY);
        outputBuffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(NMFluids.STILL_ENRICHED_BLOOD), TypedFluidBuffer.Mode.EXTRACT_ONLY);
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
    public void registerControllers(AnimationData data)
    {
        data.addAnimationController(
            new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
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
        long transferred = inputBuffer.extractDirect(inputBuffer.getResource(), decrement, transaction);
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
            long extracted = inputBuffer.extractDirect(FluidVariant.of(NMFluids.STILL_BLOOD), conversionAmount, transaction);
            long inserted = outputBuffer.insertDirect(FluidVariant.of(NMFluids.STILL_ENRICHED_BLOOD), extracted, transaction);
        }
        transaction.commit();
    }

    @Override
    public Storage<FluidVariant> getBuffer(Direction direction)
    {
        return buffer;
    }

    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event)
    {
        event.getController().transitionLengthTicks = 20;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.integrator.hatch", true));
        return PlayState.CONTINUE;
    }
}
