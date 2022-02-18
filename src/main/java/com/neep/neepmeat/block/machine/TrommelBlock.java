package com.neep.neepmeat.block.machine;

import com.neep.neepmeat.block.base.BaseHorFacingBlock;
import com.neep.neepmeat.blockentity.TrommelBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class TrommelBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public final VoxelShape[] SHAPES = {
            VoxelShapes.cuboid(0f, -1d, -1d, 1d, 2d, 2d),
            VoxelShapes.cuboid(-1f, -1d, 0d, 2d, 2d, 1d)
    };

    public TrommelBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
//        this.setDefaultState(getDefaultState().with(CENTRE, false).with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        Direction direction = context.getPlayerLookDirection();
        return direction.getAxis().isVertical() ? getDefaultState() :
                this.getDefaultState().with(FACING, direction);
    }

    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPES[(state.get(FACING).getAxis().equals(Direction.Axis.X)) ? 0 : 1];
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return state.getOutlineShape(world, pos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new TrommelBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }
}
