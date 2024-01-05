package com.neep.neepmeat.machine.large_motor;

import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class LargeMotorStructureEntity extends BigBlockStructureEntity
{
    public LargeMotorStructureEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
