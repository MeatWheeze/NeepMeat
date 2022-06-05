package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class LinearOscillatorBlockEntity extends BlockEntity
{
    public float offset = 0;
    public boolean extended = false;

    public LinearOscillatorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.LINEAR_OSCILLATOR, pos, state);
    }

    public LinearOscillatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
