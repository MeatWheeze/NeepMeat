package com.neep.neepmeat.machine.bottler;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class BottlerBlockEntity extends BlockEntity
{
    public BottlerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BottlerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.BOTTLER, pos, state);
    }
}
