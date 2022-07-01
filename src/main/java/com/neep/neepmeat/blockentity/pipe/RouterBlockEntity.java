package com.neep.neepmeat.blockentity.pipe;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class RouterBlockEntity extends BlockEntity
{
    public RouterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ROUTER, pos, state);
    }

    public RouterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
