package com.neep.neepmeat.machine;

import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatable
{
    default void setBurning()
    {
        if (this instanceof FurnaceAccessor accessor)
        {
            accessor.setBurnTime(2);
        }
    }

    void updateState(World world, BlockPos pos, BlockState oldState);

    default int getCurrentBurnTime()
    {
        if (this instanceof FurnaceAccessor accessor)
        {
            return accessor.getBurnTime();
        }
        return 0;
    }
}
