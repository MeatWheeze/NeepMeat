package com.neep.meatlib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class BaseVertFacingBlock extends BaseBlock
{
    public static final DirectionProperty FACING = Properties.VERTICAL_DIRECTION;

    public BaseVertFacingBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    public BaseVertFacingBlock(String registryName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, factory, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        Direction facing = ctx.getPlayer().getPitch() > Math.PI ? Direction.UP : Direction.DOWN;
        if (ctx.getPlayer().isSneaking())
            facing = facing.getOpposite();
        return this.getDefaultState().with(FACING, facing);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }
}
