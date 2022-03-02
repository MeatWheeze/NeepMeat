package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid_util.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.FluidInitialiser;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class BloodMachineBlockEntity<T extends BloodMachineBlockEntity> extends BlockEntity implements FluidBuffer.FluidBufferProvider
{
    protected TypedFluidBuffer buffer;

    public BloodMachineBlockEntity(BlockEntityType<T> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        buffer = new TypedFluidBuffer(this, 4 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(FluidInitialiser.STILL_ENRICHED_BLOOD));
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
