package com.neep.neepmeat.machine.surgery_platform;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.api.pipe.IDataCable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class SurgeryPlatformBlock extends BaseBlock implements IDataCable
{
    public SurgeryPlatformBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return IDataCable.super.connectInDirection(world, pos, state, direction);
    }
}
