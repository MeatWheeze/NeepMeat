package com.neep.neepbus.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepbus.NeepBus;
import com.neep.neepbus.screen.SimpleScreenHandlerFactory;
import com.neep.neepbus.block.entity.LinearLeverBlockEntity;
import com.neep.neepbus.screen.SliderScreenHandler;
import com.neep.neepbus.screen.NeepBusConfigScreenHandler;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class LinearLeverBlock extends WallMountedBlock implements BlockEntityProvider, NeepBusProvider, DataCable
{
    public static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 10.0, 12.0, 15.0, 16.0);
    public static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 0.0, 12.0, 15.0, 6.0);
    public static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 1.0, 4.0, 16.0, 15.0, 12.0);
    public static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 1.0, 4.0, 6.0, 15.0, 12.0);
    public static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 1.0, 12.0, 6.0, 15.0);
    public static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 0.0, 4.0, 15.0, 6.0, 12.0);
    public static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 1.0, 12.0, 16.0, 15.0);
    public static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 10.0, 4.0, 15.0, 16.0, 12.0);

    public LinearLeverBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        itemSettings.create(this, ctx, itemSettings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return true;
    }

//    @Nullable
//    @Override
//    public BlockState getPlacementState(ItemPlacementContext ctx)
//    {
//        for (Direction direction : ctx.getPlacementDirections())
//        {
//            BlockState blockState;
//            if (direction.getAxis() == Direction.Axis.Y)
//            {
//                blockState = this.getDefaultState()
//                        .with(FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
//                        .with(FACING, ctx.getHorizontalPlayerFacing());
//            }
//            else
//            {
//                blockState = this.getDefaultState().with(FACE, WallMountLocation.WALL).with(FACING, direction.getOpposite());
//            }
//
//            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos()))
//            {
//                return blockState;
//            }
//        }
//
//        return null;
//    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof LinearLeverBlockEntity be)
        {
            if (!player.isSneaking())
            {
                player.openHandledScreen((SimpleScreenHandlerFactory) (syncId, playerInventory, player1) ->
                        new SliderScreenHandler(syncId, playerInventory, be));
            }
            else
            {
                player.openHandledScreen(NeepBusConfigScreenHandler.getFactory(be.getConfig()));
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NeepBus.LINEAR_LEVER_BE.instantiate(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        switch (state.get(FACE))
        {
            case FLOOR ->
            {
                return switch ((state.get(FACING)).getAxis())
                {
                    case X -> FLOOR_X_AXIS_SHAPE;
                    default -> FLOOR_Z_AXIS_SHAPE;
                };
            }
            case WALL ->
            {
                return switch (state.get(FACING))
                {
                    case EAST -> EAST_WALL_SHAPE;
                    case WEST -> WEST_WALL_SHAPE;
                    case SOUTH -> SOUTH_WALL_SHAPE;
                    default -> NORTH_WALL_SHAPE;
                };
            }
            default ->
            {
                return switch (state.get(FACING).getAxis())
                {
                    case X -> CEILING_X_AXIS_SHAPE;
                    default -> CEILING_Z_AXIS_SHAPE;
                };
            }
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACE, FACING);
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {
        if (world.getBlockEntity(pos) instanceof LinearLeverBlockEntity be)
        {
            be.updateNetwork();
        }
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return DataCable.super.connectInDirection(world, pos, state, direction);
    }

    @Override
    public boolean isConnected(World world, BlockPos pos, BlockState state, Direction direction)
    {
        WallMountLocation face = state.get(FACE);
        return (direction == Direction.UP && face == WallMountLocation.CEILING)
                || (direction == Direction.DOWN && face == WallMountLocation.FLOOR)
                || (face == WallMountLocation.WALL && direction.getOpposite() == state.get(FACING));
    }
}
