package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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

    public BloodMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long inCapacity, long outCapacity)
    {
        super(type, pos, state);
        inputBuffer = new TypedFluidBuffer(this, inCapacity, fluidVariant -> fluidVariant.equals(NMFluids.CHARGED), TypedFluidBuffer.Mode.INSERT_ONLY);
        outputBuffer = new TypedFluidBuffer(this, outCapacity, fluidVariant -> fluidVariant.equals(NMFluids.UNCHARGED), TypedFluidBuffer.Mode.EXTRACT_ONLY);
        buffer = new MultiTypedFluidBuffer(this, List.of(inputBuffer, outputBuffer));
    }

    public long doWork(long amount, Transaction transaction)
    {
        Transaction nested = transaction.openNested();
        long extracted = inputBuffer.extractDirect(NMFluids.CHARGED, amount, transaction);
        long inserted = outputBuffer.insertDirect(NMFluids.UNCHARGED, extracted, transaction);
        if (extracted == amount && inserted == amount)
        {
            nested.commit();
            return extracted;
        }
        nested.abort();
        return 0;
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
