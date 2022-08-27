package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MergePipeBlockEntity extends PneumaticPipeBlockEntity
{
    public MergePipeBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MERGE_ITEM_PIPE, pos, state);
    }

    public MergePipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}

