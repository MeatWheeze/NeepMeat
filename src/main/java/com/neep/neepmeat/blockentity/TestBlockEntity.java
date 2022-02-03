package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class TestBlockEntity extends BlockEntity
{
    public TestBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.TEST_BLOCK_ENTITY, pos, state);
    }
}
