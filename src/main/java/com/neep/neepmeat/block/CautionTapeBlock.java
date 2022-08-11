package com.neep.neepmeat.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CautionTapeBlock extends BaseBlock
{
    public static final EnumProperty<WireConnection> CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, CONNECTION_NORTH, Direction.EAST, CONNECTION_EAST, Direction.SOUTH, CONNECTION_SOUTH, Direction.WEST, CONNECTION_WEST));
    public BlockState dotState;

    public CautionTapeBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(CONNECTION_NORTH, WireConnection.NONE).with(CONNECTION_EAST, WireConnection.NONE).with(CONNECTION_SOUTH, WireConnection.NONE).with(CONNECTION_WEST, WireConnection.NONE));
        this.dotState = this.getDefaultState().with(CONNECTION_NORTH, WireConnection.SIDE).with(CONNECTION_EAST, WireConnection.SIDE).with(CONNECTION_SOUTH, WireConnection.SIDE).with(CONNECTION_WEST, WireConnection.SIDE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getPlacementState(ctx.getWorld(), dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos)
    {
        state = this.getDefaultWireState(world, getDefaultState(), pos);
        if (isNotConnected(state))
        {
            return state;
        }
        boolean north = state.get(CONNECTION_NORTH).isConnected();
        boolean east = state.get(CONNECTION_SOUTH).isConnected();
        boolean south = state.get(CONNECTION_EAST).isConnected();
        boolean west = state.get(CONNECTION_WEST).isConnected();
        boolean bl6 = !north && !east;
        boolean bl7 = !south && !west;
        if (!west && bl6)
        {
            state = state.with(CONNECTION_WEST, WireConnection.SIDE);
        }
        if (!south && bl6)
        {
            state = state.with(CONNECTION_EAST, WireConnection.SIDE);
        }
        if (!north && bl7)
        {
            state = state.with(CONNECTION_NORTH, WireConnection.SIDE);
        }
        if (!east && bl7)
        {
            state = state.with(CONNECTION_SOUTH, WireConnection.SIDE);
        }
        return state;
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos)
    {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            if (state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected()) continue;
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
            state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        if (direction == Direction.DOWN)
        {
            return state;
        }
        if (direction == Direction.UP)
        {
            return this.getPlacementState(world, state, pos);
        }
        WireConnection connection = this.getRenderConnectionType(world, pos, direction, true);
        if (connection.isConnected() == state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() && !isFullyConnected(state))
        {
            return state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), connection);
        }
        return this.getPlacementState(world, this.dotState.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), connection), pos);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir)
    {
        return state.isOf(NMBlocks.CAUTION_TAPE);
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl)
    {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl && this.canRunOnTop(world, blockPos, blockState) && connectsTo(world.getBlockState(blockPos.up()), null))
        {
            if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite()))
            {
                return WireConnection.UP;
            }
            return WireConnection.SIDE;
        }
        if (connectsTo(blockState, direction))
        {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    private static boolean isFullyConnected(BlockState state)
    {
        return state.get(CONNECTION_NORTH).isConnected() && state.get(CONNECTION_SOUTH).isConnected() && state.get(CONNECTION_EAST).isConnected() && state.get(CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state)
    {
        return !state.get(CONNECTION_NORTH).isConnected() && !state.get(CONNECTION_SOUTH).isConnected() && !state.get(CONNECTION_EAST).isConnected() && !state.get(CONNECTION_WEST).isConnected();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor)
    {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void updateNeighbors(World world, BlockPos pos)
    {
        if (!world.getBlockState(pos).isOf(this))
        {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values())
        {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (oldState.isOf(state.getBlock()) || world.isClient)
        {
            return;
        }
        for (Direction direction : Direction.Type.VERTICAL)
        {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (moved || state.isOf(newState.getBlock()))
        {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world.isClient)
        {
            return;
        }
        for (Direction direction : Direction.values())
        {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(CONNECTION_NORTH, CONNECTION_EAST, CONNECTION_SOUTH, CONNECTION_WEST);
    }
}
