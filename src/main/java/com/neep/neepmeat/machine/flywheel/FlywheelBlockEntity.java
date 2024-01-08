package com.neep.neepmeat.machine.flywheel;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class FlywheelBlockEntity extends SyncableBlockEntity
{
    public FlywheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
