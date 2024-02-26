package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class WrithingStoneBlock extends BaseBlock
{
    public WrithingStoneBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true;
    }

    @Override
    public boolean autoGenDrop()
    {
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        BlockState spreadState = NMBlocks.CONTAMINATED_DIRT.getDefaultState();
        BlockState downState = NMBlocks.WRITHING_STONE.getDefaultState();

        for (int i = 0; i < 4; ++i)
        {
            BlockPos spreadPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 4, random.nextInt(3) - 1);
            if (canSpread(world.getBlockState(spreadPos), world, spreadPos))
            {
                if (spreadPos.getX() == pos.getX() && spreadPos.getZ() == pos.getZ())
                    world.setBlockState(spreadPos, downState);
                else
                    world.setBlockState(spreadPos, spreadState);
            }
        }
    }

    private boolean canSpread(BlockState blockState, ServerWorld world, BlockPos spreadPos)
    {
//        return blockState.isAir();
        return blockState.isIn(BlockTags.STONE_ORE_REPLACEABLES) || blockState.isIn(BlockTags.DIRT);
    }
}
