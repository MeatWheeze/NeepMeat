package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_transfer.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class LargeConverterBlockEntity extends BlockEntity
{
    protected TypedFluidBuffer rawBuffer;
    protected TypedFluidBuffer outputBuffer;
    protected MultiTypedFluidBuffer buffer;

    protected boolean isAssembled;

    public LargeConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        rawBuffer = new TypedFluidBuffer(this, 4 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(NMFluids.STILL_ENRICHED_BLOOD), TypedFluidBuffer.Mode.INSERT_ONLY);
        outputBuffer = new TypedFluidBuffer(this, 4 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(NMFluids.STILL_BLOOD), TypedFluidBuffer.Mode.EXTRACT_ONLY);
        buffer = new MultiTypedFluidBuffer(this, List.of(rawBuffer, outputBuffer));
    }

    public LargeConverterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.LARGE_CONVERTER, pos, state);
    }

    public long convert(long amount, Transaction transaction)
    {
        Transaction nested = transaction.openNested();
        long extracted = rawBuffer.extractDirect(FluidVariant.of(NMFluids.STILL_ENRICHED_BLOOD), amount, transaction);
        long inserted = outputBuffer.insertDirect(FluidVariant.of(NMFluids.STILL_BLOOD), extracted, transaction);
        if (extracted == amount && inserted == amount)
        {
            nested.commit();
            return extracted;
        }
        nested.abort();
        return 0;
    }
}
