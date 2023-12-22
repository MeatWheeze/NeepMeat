package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MixerTopBlockEntity extends SyncableBlockEntity
{
    public MixerTopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MixerTopBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MIXER_TOP, pos, state);
    }
}
