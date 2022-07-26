package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class BloodMachineBlockEntity extends SyncableBlockEntity implements FluidBuffer.FluidBufferProvider
{
    public TypedFluidBuffer inputBuffer;
    public TypedFluidBuffer outputBuffer;
    protected MultiTypedFluidBuffer buffer;

    public BloodMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long inCapacity, long outCapacity)
    {
        super(type, pos, state);
        inputBuffer = new TypedFluidBuffer(inCapacity, fluidVariant -> fluidVariant.equals(NMFluids.CHARGED), TypedFluidBuffer.Mode.INSERT_ONLY, this::sync);
        outputBuffer = new TypedFluidBuffer(outCapacity, fluidVariant -> fluidVariant.equals(NMFluids.UNCHARGED), TypedFluidBuffer.Mode.EXTRACT_ONLY, this::sync);
        buffer = new MultiTypedFluidBuffer(this, List.of(inputBuffer, outputBuffer));
    }

    public long doWork(long amount, TransactionContext transaction)
    {
        Transaction nested = transaction.openNested();
        long extracted = inputBuffer.extractDirect(NMFluids.CHARGED, amount, nested);
        long inserted = outputBuffer.insertDirect(NMFluids.UNCHARGED, extracted, nested);
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
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        buffer.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        buffer.readNbt(nbt);
    }

    public void onUse(PlayerEntity player, Hand hand)
    {

    }

    public void clearBuffers()
    {
        this.inputBuffer.clear();
        this.outputBuffer.clear();
    }
}
