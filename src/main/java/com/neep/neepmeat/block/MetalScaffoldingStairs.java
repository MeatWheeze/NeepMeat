package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseStairsBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.state.StateManager;

public class MetalScaffoldingStairs extends BaseStairsBlock implements Waterloggable
{
    public MetalScaffoldingStairs(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, NMBlocks.SCAFFOLD_PLATFORM.getDefaultState(), itemSettings, settings.nonOpaque());
    }

//    @Override
//    public BlockState getPlacementState(ItemPlacementContext ctx)
//    {
//        BlockPos blockPos = ctx.getBlockPos();
//        World world = ctx.getWorld();
//        return (this.getDefaultState()
//                .with(WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER))
//                .with(BOTTOM, this.shouldBeBottom(world, blockPos));
//    }

//    @Override
//    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
//    {
//        state = state.with(BOTTOM, world.getBlockState(pos.up()).getBlock() instanceof MetalScaffoldingStairs);
//
//        if (state.get(WATERLOGGED))
//        {
//            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
//        }
//        if (!world.isClient())
//        {
//            world.getBlockTickScheduler().schedule(pos, this, 1);
//        }
//        return state;
//    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
    }

}
