package com.neep.neepmeat.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class BloodBubblePressurePlate extends PressurePlateBlock
{
    public BloodBubblePressurePlate(ActivationRule type, Settings settings)
    {
        super(type, settings);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return 0;
    }
}
