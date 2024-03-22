package com.neep.neepmeat.machine.large_crusher;

import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class LargeCrusherStructureBlockEntity extends BigBlockStructureEntity
{
    public LargeCrusherStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
