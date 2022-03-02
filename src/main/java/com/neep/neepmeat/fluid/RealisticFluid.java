package com.neep.neepmeat.fluid;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

public class RealisticFluid extends FlowableFluid
{
    @Override
    public boolean matchesType(Fluid fluid)
    {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    public Fluid getFlowing()
    {
        return null;
    }

    @Override
    public Fluid getStill()
    {
        return null;
    }

    @Override
    protected boolean isInfinite()
    {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)
    {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction)
    {
        return false;
    }

    @Override
    protected int getFlowSpeed(WorldView world)
    {
        return 10;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world)
    {
        return 1;
    }

    @Override
    public Item getBucketItem()
    {
        return null;
    }

    @Override
    public int getTickRate(WorldView world)
    {
        return 10;
    }

    @Override
    protected float getBlastResistance()
    {
        return 100;
    }

    @Override
    protected BlockState toBlockState(FluidState state)
    {
        return null;
    }

    @Override
    public boolean isStill(FluidState state)
    {
        return false;
    }

    @Override
    public int getLevel(FluidState state)
    {
        return 0;
    }

    public static void incrementLevel(World world, BlockPos pos, BlockState state, FlowableFluid fluid)
    {
        FluidState fluidstate = world.getFluidState(pos);
        if (!fluidstate.isEmpty() && !fluidstate.getFluid().equals(fluid))
            return;

        if (fluidstate.isEmpty() && state.isOf(Blocks.AIR))
        {
            world.setBlockState(pos, fluid.getFlowing(1, false).getBlockState(), Block.NOTIFY_ALL);
            return;
        }

        int level = fluidstate.getLevel();
        System.out.println("level: " + level);
//        world.setBlockState(pos, fluid.getFlowing(8, false).getBlockState(), Block.NOTIFY_ALL);
        world.setBlockState(pos, fluid.getFlowing(level + 1, false).getBlockState(), Block.NOTIFY_ALL);
    }

    protected void tryFlow(WorldAccess world, BlockPos fluidPos, FluidState state)
    {
        if (state.isEmpty())
        {
            return;
        }

        // Try to distribute current level across surrounding blocks.

        List<BlockPos> posList = new ArrayList<>();
        for (Direction direction : new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST})
        {
            BlockPos targetPos = fluidPos.offset(direction, 1);
            BlockState targetState = world.getBlockState(targetPos);
            int targetLevel = targetState.getFluidState().getLevel();
            if (canFill(world, targetPos, targetState, this) && (targetLevel < state.getLevel())
                    && (state.getLevel() > 1 || direction == Direction.DOWN))
            {
                posList.add(targetPos);
            }
        }

        // Prioritise neighbours with lower fluid levels.
        // Distribute fluid across list of positions
        int level = state.getLevel();
        for (BlockPos blockPos : posList)
        {
            // Break if there is too little fluid, but also allow flowing down
            if (!(level > 1 || fluidPos.offset(Direction.DOWN).equals(blockPos)))
                break;

            BlockPos pos = blockPos;
            BlockState targetState = world.getBlockState(pos);
            int targetLevel = targetState.getFluidState().getLevel();

            world.setBlockState(pos, this.getFlowing(targetLevel + 1, false).getBlockState(), Block.NOTIFY_ALL);
            --level;

        }
        if (level != state.getLevel())
        {
            BlockState newState = level > 0 ? this.getFlowing(level, false).getBlockState() : Blocks.AIR.getDefaultState();
            world.setBlockState(fluidPos, newState, Block.NOTIFY_ALL);
        }
    }

    private boolean canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid)
    {
        Block block = state.getBlock();
        if (block instanceof FluidFillable) {
            return ((FluidFillable)((Object)block)).canFillWithFluid(world, pos, state, fluid);
        }
        if (block instanceof DoorBlock || state.isIn(BlockTags.SIGNS) || state.isOf(Blocks.LADDER) || state.isOf(Blocks.SUGAR_CANE) || state.isOf(Blocks.BUBBLE_COLUMN)) {
            return false;
        }
        Material material = state.getMaterial();
        if (material == Material.PORTAL || material == Material.STRUCTURE_VOID || material == Material.UNDERWATER_PLANT || material == Material.REPLACEABLE_UNDERWATER_PLANT) {
            return false;
        }
        return !material.blocksMovement();
    }
}
