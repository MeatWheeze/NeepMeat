package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseBlock;
import com.neep.neepmeat.block.base.BaseStairsBlock;
import com.neep.neepmeat.block.base.NMBlock;
import com.neep.neepmeat.init.BlockInitialiser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MetalScaffoldingStairs extends BaseStairsBlock implements Waterloggable
{
    public MetalScaffoldingStairs(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(BlockInitialiser.SCAFFOLD_PLATFORM.getDefaultState(), itemName, itemMaxStack, settings.nonOpaque());
        registryName = itemName;
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
