package com.neep.neepmeat.block.base;

import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.PillarBlock;

public class BaseColumnBlock extends PillarBlock implements NMBlock
{
    BaseBlockItem blockItem;
    private String regsitryName;

    public BaseColumnBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
//        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.regsitryName = itemName;
    }

    @Override
    public String getRegistryName()
    {
        return regsitryName;
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
