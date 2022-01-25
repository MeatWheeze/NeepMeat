package com.neep.neepmeat.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FluidTestBlock extends Block
{
    public static final IntProperty LEVEL = Properties.LEVEL_15;

    public FluidTestBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        if (state.getFluidState().isStill() || neighborState.getFluidState().isStill())
        {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), 5);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
//        if (this.receiveNeighborFluids(world, pos, state)) {
//            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
//        }
    }

}
