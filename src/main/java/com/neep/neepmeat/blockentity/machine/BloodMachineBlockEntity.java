package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid_util.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.fluid_util.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.FluidInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public abstract class BloodMachineBlockEntity<T extends BloodMachineBlockEntity> extends BlockEntity implements FluidBuffer.FluidBufferProvider, BlockEntityClientSerializable
{
    public TypedFluidBuffer inputBuffer;
    public TypedFluidBuffer outputBuffer;
    protected MultiTypedFluidBuffer buffer;

    public BloodMachineBlockEntity(BlockEntityType<T> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        inputBuffer = new TypedFluidBuffer(this, 4 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(FluidInitialiser.STILL_ENRICHED_BLOOD), TypedFluidBuffer.Mode.INSERT_ONLY);
        outputBuffer = new TypedFluidBuffer(this, 4 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(FluidInitialiser.STILL_BLOOD), TypedFluidBuffer.Mode.EXTRACT_ONLY);
        buffer = new MultiTypedFluidBuffer(this, List.of(inputBuffer, outputBuffer));
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        buffer.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        buffer.readNbt(nbt);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        writeNbt(tag);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        readNbt(nbt);
    }
}
