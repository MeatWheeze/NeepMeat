package com.neep.neepmeat.machine;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatable
{
    void setBurning();

    void updateState(World world, BlockPos pos, BlockState oldState);
}
