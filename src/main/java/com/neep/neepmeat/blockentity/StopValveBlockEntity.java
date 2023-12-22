package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class StopValveBlockEntity extends BlockEntity
{
    public float openDelta = 0;

    public StopValveBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.STOP_VALVE, pos, state);
    }

    public StopValveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
