package com.neep.neepmeat.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

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
//            world.setBlockState(pos, fluid.getStill().getDefaultState().with(LEVEL, 1).getBlockState(), Block.NOTIFY_ALL);
//            return;
        }

        int level = fluidstate.getLevel();
        System.out.println(level);
//        world.setBlockState(pos, fluid.getFlowing(8, false).getBlockState(), Block.NOTIFY_ALL);
        world.setBlockState(pos, fluid.getFlowing().getDefaultState().with(LEVEL, 2).getBlockState(), Block.NOTIFY_ALL);
    }
}
