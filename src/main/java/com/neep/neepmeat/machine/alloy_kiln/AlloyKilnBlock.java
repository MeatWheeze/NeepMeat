package com.neep.neepmeat.machine.alloy_kiln;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUitls;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlloyKilnBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public static final BooleanProperty LIT = Properties.LIT;
    public static final VoxelShape COLLISION_SHAPE = VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 18, 16),
            Block.createCuboidShape(1, 18, 1, 15, 20, 15),
            Block.createCuboidShape(3, 20, 3, 13, 22, 13)
            );

    public AlloyKilnBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
        this.setDefaultState(getStateManager().getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        Direction facing = context.getPlayerFacing();
        return facing.getAxis().isVertical() ? getDefaultState() :
                this.getDefaultState().with(FACING, facing.getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return COLLISION_SHAPE;
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos)
    {
        return super.createScreenHandlerFactory(state, world, pos);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUitls.checkType(type, NMBlockEntities.ALLOY_KILN, AlloyKilnBlockEntity::serverTick, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new AlloyKilnBlockEntity(pos, state);
    }
}
