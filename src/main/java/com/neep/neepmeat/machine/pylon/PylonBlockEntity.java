package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class PylonBlockEntity extends SyncableBlockEntity
{
    public float angle;

    public PylonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public PylonBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.PYLON, pos, state);
    }
}
