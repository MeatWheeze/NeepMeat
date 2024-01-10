package com.neep.neepmeat.machine.well_head;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class WellHeadBlockEntity extends SyncableBlockEntity
{
    public WellHeadBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
