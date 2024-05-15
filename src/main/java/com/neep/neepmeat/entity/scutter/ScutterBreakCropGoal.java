package com.neep.neepmeat.entity.scutter;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class ScutterBreakCropGoal extends Goal
{
    private long lastStartTime = 0;

    private final FarmingScutter entity;
    private final World world;

    public ScutterBreakCropGoal(FarmingScutter entity)
    {
        this.entity = entity;
        this.world = entity.getWorld();
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean shouldRunEveryTick()
    {
        return false;
    }

    @Override
    public void tick()
    {
    }

    @Override
    public boolean canStart()
    {
        if (world.getTime() < lastStartTime + 10)
            return false;

        return canHarvest(world, entity.getBlockPos()) ||
                canHarvest(world, entity.getBlockPos().up());
    }

    @Override
    public void start()
    {
        lastStartTime = world.getTime();
        if (!tryHarvest(world, entity.getBlockPos()))
        {
            tryHarvest(world, entity.getBlockPos().up());
        }
    }

    @Override
    public boolean shouldContinue()
    {
        return false;
    }

    private boolean tryHarvest(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMature(state))
        {
            entity.harvest(world, pos, state, cropBlock);
            return true;
        }
        return false;
    }

    private boolean canHarvest(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMature(state);
    }

}
