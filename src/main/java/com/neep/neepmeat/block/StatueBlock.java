package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseHorFacingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class StatueBlock extends BaseHorFacingBlock
{
    public static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 20, 12);

    public StatueBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return getDefaultState().with(FACING, context.getPlayerFacing());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }
}
