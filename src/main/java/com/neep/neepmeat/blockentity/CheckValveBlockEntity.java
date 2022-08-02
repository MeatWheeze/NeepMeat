package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class CheckValveBlockEntity extends BlockEntity implements Storage<FluidVariant>
{
    // Maximum amount for the implicit buffer. Represents the max apparent flow rate through the block.
    public static long MAX_BUFFER_AMOUNT = 2 * FluidConstants.BUCKET;

    protected WritableFluidBuffer buffer = new WritableFluidBuffer(this, MAX_BUFFER_AMOUNT);
    protected long apparentFLow = 0;

    public CheckValveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public CheckValveBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CHECK_VALVE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        long amount = buffer.insert(resource, maxAmount, transaction);
        this.apparentFLow = amount;
        return amount;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        return buffer.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        return buffer.iterator(transaction);
    }

    public float getApparentFlow()
    {
        // TODO: Change these to longs somehow
        return apparentFLow / (float) PipeNetwork.BASE_TRANSFER;
    }
}
