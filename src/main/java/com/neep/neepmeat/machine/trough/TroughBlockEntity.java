package com.neep.neepmeat.machine.trough;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TroughBlockEntity extends SyncableBlockEntity
{
    public static final FluidVariant RESOURCE = FluidVariant.of(NMFluids.STILL_FEED);
    public static final long USE_AMOUNT = FluidConstants.BUCKET / 4;

    private WritableSingleFluidStorage storage = new WritableSingleFluidStorage(2 * FluidConstants.BUCKET, this::sync);

    public TroughBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TroughBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FEEDING_TROUGH, pos, state);
    }

    public WritableSingleFluidStorage getStorage(Direction direction)
    {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.toNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }
}
