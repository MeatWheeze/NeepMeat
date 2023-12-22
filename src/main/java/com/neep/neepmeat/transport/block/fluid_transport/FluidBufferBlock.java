package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.ImmutableMap;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.api.pipe.FluidPipe;
import com.neep.neepmeat.transport.machine.fluid.FluidBufferBlockEntity;
import com.neep.neepmeat.util.ItemUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidBufferBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public static final BooleanProperty NORTH_CONNECTION = BooleanProperty.of("north");
    public static final BooleanProperty EAST_CONNECTION = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH_CONNECTION = BooleanProperty.of("south");
    public static final BooleanProperty WEST_CONNECTION = BooleanProperty.of("west");
    public static final BooleanProperty UP_CONNECTION = BooleanProperty.of("up");
    public static final BooleanProperty DOWN_CONNECTION = BooleanProperty.of("down");

    public static final Map<Direction, BooleanProperty> DIR_TO_CONNECTION = (new ImmutableMap.Builder<Direction, BooleanProperty>()
            .put(Direction.NORTH, NORTH_CONNECTION)
            .put(Direction.EAST, EAST_CONNECTION)
            .put(Direction.SOUTH, SOUTH_CONNECTION)
            .put(Direction.WEST, WEST_CONNECTION)
            .put(Direction.UP, UP_CONNECTION)
            .put(Direction.DOWN, DOWN_CONNECTION)
    ).build();

    public static final VoxelShape X_SHAPE = Block.createCuboidShape(0, 3, 3, 16, 13, 13);
    public static final VoxelShape Y_SHAPE = Block.createCuboidShape(3, 0, 3, 13, 16, 13);
    public static final VoxelShape Z_SHAPE = Block.createCuboidShape(3, 3, 0, 13, 13, 16);

    public FluidBufferBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FluidBufferBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        Direction facing = state.get(FACING);
        return switch (facing.getAxis())
        {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        Direction facing = state.get(FACING);
        return switch (facing.getAxis())
                {
                    case X -> X_SHAPE;
                    case Y -> Y_SHAPE;
                    case Z -> Z_SHAPE;
                };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof FluidBufferBlockEntity tank && tank.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        if (!world.isClient())
        {
            return getConnectedState(world, state, pos);
        }
        return state;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState state = this.getDefaultState().with(FACING, ctx.getPlayer().isSneaking() ? ctx.getSide() : ctx.getSide().getOpposite());
        return this.getConnectedState(ctx.getWorld(), state, ctx.getBlockPos());
    }

    public BlockState getConnectedState(WorldAccess world, BlockState state, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            BlockState pipeState = world.getBlockState(pos.offset(direction));
            if (pipeState.getBlock() instanceof FluidPipe pipe && pipe.getConnections(pipeState, (d) -> true).contains(direction.getOpposite()))
//                    && pipeState.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction.getOpposite())).isConnected())
            {
                state = state.with(DIR_TO_CONNECTION.get(direction), true);
            }
            else
            {
                state = state.with(DIR_TO_CONNECTION.get(direction), false);
            }
        }
        return state;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(NORTH_CONNECTION, EAST_CONNECTION, SOUTH_CONNECTION, WEST_CONNECTION, UP_CONNECTION, DOWN_CONNECTION);
    }
}
