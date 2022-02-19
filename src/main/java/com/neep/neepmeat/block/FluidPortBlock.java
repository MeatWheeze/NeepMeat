package com.neep.neepmeat.block;

import com.google.common.collect.Maps;
import com.neep.neepmeat.block.base.BaseFacingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.Map;

public class FluidPortBlock extends BaseFacingBlock implements DirectionalFluidAcceptor
{
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

//    private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
//            Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 15.75, 1),
//            Direction.SOUTH, Block.createCuboidShape(3, 0, 15, 13, 15.75, 16),
//            Direction.WEST, Block.createCuboidShape(0, 0, 3, 1, 15.75, 13),
//            Direction.EAST, Block.createCuboidShape(15, 0, 3, 16, 15.75, 13)));

    public FluidPortBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
            return this.getDefaultState().with(FACING, context.getSide());
    }

    @Override
    public boolean connectInDirection(BlockState state, Direction direction)
    {
        return state.get(FACING) == direction;
    }

}
