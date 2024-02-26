package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WrithingEarthSpoutBlock extends BaseBlock implements BlockEntityProvider
{
    public WrithingEarthSpoutBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public boolean autoGenDrop()
    {
        return false;
    }

    private boolean canSpread(BlockState state, World world, BlockPos pos, int distance, boolean air)
    {
        if (!state.isIn(BlockTags.DIRT) && !(air && state.isAir()))
            return false;

        BlockPos.Mutable mutable = pos.mutableCopy();
        int neighbourCount = 0;

        for (Direction direction : Direction.values())
        {
            mutable.set(pos, direction);
            BlockState neighbour = world.getBlockState(mutable);

            if (neighbour.isOf(NMBlocks.CONTAMINATED_DIRT) || neighbour.isOf(NMBlocks.WRITHING_EARTH_SPOUT))
                neighbourCount++;
        }
        return neighbourCount == 1;
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true;
    }

    boolean propagate(World world, BlockPos pos, BlockState state, int distance, int maxDistance, Random random, boolean air)
    {
        if (distance >= maxDistance)
            return false;

        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        Collections.shuffle(directions);
        for (Direction direction : directions)
        {
            BlockPos newPos = pos.offset(direction);
            BlockState newState = world.getBlockState(newPos);

            if (newState.isOf(NMBlocks.WRITHING_EARTH_SPOUT) || newState.isOf(NMBlocks.CONTAMINATED_DIRT))
            {
                if (propagate(world, newPos, newState, distance + 1, maxDistance, random, air))
                    return true;
            }
            else if (canSpread(newState, world, newPos, distance, air))
            {
                world.setBlockState(newPos, NMBlocks.CONTAMINATED_DIRT.getDefaultState());
                return true;
            }
        }
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        propagate(world, pos, state, 0, 7, random, !world.getBlockState(pos.up()).isOf(NMBlocks.WELL_HEAD));
//        if (!canSurvive(state, world, pos))
//        {
//            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
//        }
//        else
//        {
//            BlockState spreadState = NMBlocks.CONTAMINATED_DIRT.getDefaultState();
//
//            for(int i = 0; i < 4; ++i)
//            {
//                BlockPos spreadPos = pos.add(random.nextInt(7) - 3, random.nextInt(5) - 3, random.nextInt(7) - 3);
//                if (canSpread(world.getBlockState(spreadPos), world, spreadPos))
//                {
//                    world.setBlockState(spreadPos, spreadState);
//                }
//            }
//        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.WRITHING_EARTH_SPOUT.instantiate(pos, state);
    }
}
