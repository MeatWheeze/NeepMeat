package com.neep.neepmeat.block;

import com.neep.neepmeat.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

public class BaseColumnBlock extends PillarBlock
{
    BaseBlockItem blockItem;

    public BaseColumnBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
//        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

//    @Override
//    public BlockState getPlacementState(ItemPlacementContext context)
//    {
//        return this.getDefaultState().with(FACING, context.getPlayerLookDirection());
//    }

//    @Override
//    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
//    {
//        builder.add(FACING);
//    }

}
