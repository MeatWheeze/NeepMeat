package com.neep.neepmeat.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class PipeBlock extends BaseBlock
{
//    public static final EnumProperty<PipeConnection> NORTH_CONNECTION = PipeProperties.NORTH_CONNECTION;
//    public static final EnumProperty<PipeConnection> EAST_CONNECTION = PipeProperties.EAST_CONNECTION;
//    public static final EnumProperty<PipeConnection> SOUTH_CONNECTION = PipeProperties.SOUTH_CONNECTION;
//    public static final EnumProperty<PipeConnection> WEST_CONNECTION = PipeProperties.WEST_CONNECTION;
//    public static final EnumProperty<PipeConnection> UP_CONNECTION = PipeProperties.UP_CONNECTION;
//    public static final EnumProperty<PipeConnection> DOWN_CONNECTION = PipeProperties.DOWN_CONNECTION;
    public static final BooleanProperty NORTH_CONNECTION = Properties.NORTH;
    public static final BooleanProperty EAST_CONNECTION = Properties.EAST;
    public static final BooleanProperty SOUTH_CONNECTION = Properties.SOUTH;
    public static final BooleanProperty WEST_CONNECTION = Properties.WEST;
    public static final BooleanProperty UP_CONNECTION = Properties.UP;
    public static final BooleanProperty DOWN_CONNECTION = Properties.DOWN;

    public static final Map<Direction, BooleanProperty> DIR_TO_CONNECTION = (new ImmutableMap.Builder<Direction, BooleanProperty>()
            .put(Direction.NORTH, NORTH_CONNECTION)
            .put(Direction.EAST, EAST_CONNECTION)
            .put(Direction.SOUTH, SOUTH_CONNECTION)
            .put(Direction.WEST, WEST_CONNECTION)
            .put(Direction.DOWN, DOWN_CONNECTION)
            .put(Direction.UP, UP_CONNECTION)
    ).build();

    public PipeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
        this.setDefaultState(this.stateManager.getDefaultState()
//                .with(NORTH_CONNECTION, PipeConnection.NONE)
//                .with(EAST_CONNECTION, PipeConnection.NONE)
//                .with(SOUTH_CONNECTION, PipeConnection.NONE)
//                .with(WEST_CONNECTION, PipeConnection.NONE)
//                .with(UP_CONNECTION, PipeConnection.NONE)
//                .with(DOWN_CONNECTION, PipeConnection.NONE));
                .with(NORTH_CONNECTION, false)
                .with(EAST_CONNECTION, false)
                .with(SOUTH_CONNECTION, false)
                .with(WEST_CONNECTION, false)
                .with(UP_CONNECTION, false)
                .with(DOWN_CONNECTION, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getPlacementState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos)
    {
        boolean bl7;
        boolean bl = isNotConnected(state);
        state = this.getConnections(world, this.getDefaultState(), pos);
        if (bl && isNotConnected(state))
        {
            return state;
        }
        boolean north = state.get(NORTH_CONNECTION);
        boolean south = state.get(SOUTH_CONNECTION);
        boolean east = state.get(EAST_CONNECTION);
        boolean west = state.get(WEST_CONNECTION);
        boolean up = state.get(UP_CONNECTION);
        boolean down = state.get(DOWN_CONNECTION);
        boolean nNS = !north && !south;
        boolean nEW = !east && !west;
        boolean nUD = !up && !down;
        if (!west && nNS & nUD)
        {
            state = state.with(WEST_CONNECTION, true);
        }
        if (!east && nNS && nUD)
        {
            state = state.with(EAST_CONNECTION, true);
        }
        if (!north && nEW && nUD)
        {
            state = state.with(NORTH_CONNECTION, true);
        }
        if (!south && nEW && nUD)
        {
            state = state.with(SOUTH_CONNECTION, true);
        }
        if (!up && nNS && nEW && nUD)
        {
            state = state.with(UP_CONNECTION, true);
        }
        if (!down && nNS && nEW && nUD)
        {
            state = state.with(DOWN_CONNECTION, true);
        }
        return state;
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            BlockState blockPos;
            boolean connection = state.get(DIR_TO_CONNECTION.get(direction));
            if (!connection || world.getBlockState(mutable.set(pos, direction)).isOf(this)) continue;
            mutable.move(Direction.DOWN);

            mutable.set(pos, direction).move(Direction.UP);
            blockPos = world.getBlockState(mutable);
            if (blockPos.isOf(Blocks.OBSERVER)) continue;
            BlockPos blockPos1 = mutable.offset(direction.getOpposite());
            BlockState blockState3 = blockPos.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos1), world, mutable, blockPos1);
            RedstoneWireBlock.replace(blockPos, blockState3, world, mutable, flags, maxUpdateDepth);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        boolean connection = this.connectInDirection(world, pos, direction);
        if (connection == state.get(DIR_TO_CONNECTION.get(direction)) && !isFullyConnected(state))
        {
            return state.with(DIR_TO_CONNECTION.get(direction), connection);
        }
        return this.getPlacementState(world, this.getDefaultState().with(DIR_TO_CONNECTION.get(direction), connection), pos);
    }

    // TODO: Add other things
    public boolean canConnectTo(BlockState state)
    {
        return state.getBlock() instanceof PipeBlock;
    }

    private static boolean isNotConnected(BlockState state)
    {
        return !state.get(NORTH_CONNECTION)
                && !state.get(SOUTH_CONNECTION)
                && !state.get(EAST_CONNECTION)
                && !state.get(WEST_CONNECTION)
                && !state.get(UP_CONNECTION)
                && !state.get(DOWN_CONNECTION)
                ;
    }

    private static boolean isFullyConnected(BlockState state)
    {
        return state.get(NORTH_CONNECTION)
                && state.get(SOUTH_CONNECTION)
                && state.get(EAST_CONNECTION)
                && state.get(WEST_CONNECTION)
                && state.get(UP_CONNECTION)
                && state.get(DOWN_CONNECTION)
                ;
    }

    private BlockState getConnections(BlockView world, BlockState state, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            boolean property = state.get(DIR_TO_CONNECTION.get(direction));
            if (property) continue;
            BlockPos adjPos = pos.offset(direction);
            BlockState adjState = world.getBlockState(adjPos);
            state = state.with(DIR_TO_CONNECTION.get(direction), canConnectTo(adjState));
        }
        return state;
    }

    private boolean connectInDirection(WorldAccess world, BlockPos pos, Direction direction)
    {
        BlockState targetState = world.getBlockState(pos.offset(direction));
        return canConnectTo(targetState);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH_CONNECTION, EAST_CONNECTION, SOUTH_CONNECTION, WEST_CONNECTION, UP_CONNECTION, DOWN_CONNECTION);
    }
}
