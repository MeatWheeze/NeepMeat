package com.neep.neepbus.block;

import com.neep.neepbus.block.entity.GaugeBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface GaugeProvider
{
    default GaugeBlockEntity getBlockEntity(World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof GaugeBlockEntity be)
        {
            return be;
        }
        return GaugeBlockEntity.EMPTY;
    }
}
