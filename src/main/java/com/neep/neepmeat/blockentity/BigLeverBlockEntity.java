package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class BigLeverBlockEntity extends BlockEntity
{
    public BigLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BigLeverBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.BIG_LEVER, pos, state);
    }
}
