package com.neep.neepmeat.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class AdvancedIntegratorBlockEntity extends SyncableBlockEntity
{
    public AdvancedIntegratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }
}
