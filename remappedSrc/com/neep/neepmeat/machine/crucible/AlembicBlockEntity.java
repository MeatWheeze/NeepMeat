package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class AlembicBlockEntity extends SyncableBlockEntity
{
    protected WritableSingleFluidStorage fluidStorage;

    public AlembicBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.fluidStorage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::sync)
        {
            @Override
            public boolean supportsInsertion()
            {
                return false;
            }
        };
    }

    public AlembicBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ALEMBIC, pos, state);
    }

    public WritableSingleFluidStorage getStorage(@Nullable Direction direction)
    {
        return fluidStorage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        fluidStorage.writeNbt1(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fluidStorage.readNbt(nbt);
    }
}
