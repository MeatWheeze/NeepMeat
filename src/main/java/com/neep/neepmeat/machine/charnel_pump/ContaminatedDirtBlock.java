package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class ContaminatedDirtBlock extends BaseBlock
{
    public ContaminatedDirtBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : Direction.values())
        {
            mutable.set(pos, direction);
            BlockState offsetState = world.getBlockState(mutable);
            if (offsetState.isOf(Blocks.GRASS_BLOCK))
            {
                world.setBlockState(mutable, Blocks.DIRT.getDefaultState());
                return;
            }
        }

        super.randomTick(state, world, pos, random);
    }
}
