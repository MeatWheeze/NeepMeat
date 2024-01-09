package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class CharnelPumpBlockEntity extends SyncableBlockEntity
{
    public CharnelPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
