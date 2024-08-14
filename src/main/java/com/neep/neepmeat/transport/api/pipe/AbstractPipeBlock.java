package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import com.neep.neepmeat.transport.fluid_network.PipeProperties;
import com.neep.neepmeat.util.NMMaths;
import com.neep.neepmeat.util.NMVec2f;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.EnumMap;
import java.util.Map;

public abstract class AbstractPipeBlock extends BaseBlock implements Waterloggable
{
    public static final EnumProperty<PipeConnectionType> NORTH_CONNECTION = EnumProperty.of("north", PipeConnectionType.class);
    public static final EnumProperty<PipeConnectionType> EAST_CONNECTION = PipeProperties.EAST_CONNECTION;
    public static final EnumProperty<PipeConnectionType> SOUTH_CONNECTION = PipeProperties.SOUTH_CONNECTION;
    public static final EnumProperty<PipeConnectionType> WEST_CONNECTION = PipeProperties.WEST_CONNECTION;
    public static final EnumProperty<PipeConnectionType> UP_CONNECTION = PipeProperties.UP_CONNECTION;
    public static final EnumProperty<PipeConnectionType> DOWN_CONNECTION = PipeProperties.DOWN_CONNECTION;

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private final Map<BlockState, VoxelShape> shapes = Maps.newHashMap();

//    public static final Map<Direction, EnumProperty<PipeConnectionType>> DIR_TO_CONNECTION = new ImmutableMap.Builder<Direction, EnumProperty<PipeConnectionType>>()
    public static final Map<Direction, EnumProperty<PipeConnectionType>> DIR_TO_CONNECTION = Util.make(() ->
    {
        EnumMap<Direction, EnumProperty<PipeConnectionType>> map = new EnumMap<>(Direction.class);
        map.put(Direction.NORTH, NORTH_CONNECTION);
        map.put(Direction.EAST, EAST_CONNECTION);
        map.put(Direction.SOUTH, SOUTH_CONNECTION);
        map.put(Direction.WEST, WEST_CONNECTION);
        map.put(Direction.DOWN, DOWN_CONNECTION);
        map.put(Direction.UP, UP_CONNECTION);
        return map;
    });

    public static final Map<Direction, VoxelShape> DIR_SHAPES = (new ImmutableMap.Builder<Direction, VoxelShape>()
            .put(Direction.NORTH, Block.createCuboidShape(4, 4, 0, 12, 12, 5))
            .put(Direction.EAST, Block.createCuboidShape(11, 4, 4, 16, 12, 12))
            .put(Direction.SOUTH, Block.createCuboidShape(4, 4, 11, 12, 12, 16))
            .put(Direction.WEST, Block.createCuboidShape(0, 4, 4, 5, 12, 12))
            .put(Direction.UP, Block.createCuboidShape(4, 11, 4, 12, 16, 12))
            .put(Direction.DOWN, Block.createCuboidShape(4, 0, 4, 12, 5, 12))
    ).build();

    public AbstractPipeBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, itemSettings, settings.solid());
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH_CONNECTION, PipeConnectionType.NONE)
                .with(EAST_CONNECTION, PipeConnectionType.NONE)
                .with(SOUTH_CONNECTION, PipeConnectionType.NONE)
                .with(WEST_CONNECTION, PipeConnectionType.NONE)
                .with(UP_CONNECTION, PipeConnectionType.NONE)
                .with(DOWN_CONNECTION, PipeConnectionType.NONE)
                .with(WATERLOGGED, false)
        );

        for (BlockState state : this.getStateManager().getStates())
        {
            shapes.put(state, getShapeForState(state));
        }
    }

    protected VoxelShape getCentreShape()
    {
        return Block.createCuboidShape(4, 4, 4, 12, 12, 12);
    }

    public VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape shape = getCentreShape();
        for (Direction direction : Direction.values())
        {
            if (state.get(DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
            {
                shape = VoxelShapes.union(shape, DIR_SHAPES.get(direction));
            }
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return shapes.get(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
//        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
        return shapes.get(state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        return this.getConnectedState(world, this.getDefaultState(), pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        boolean connection = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);

        // Already connected in direction
        if (connection && (state.get(DIR_TO_CONNECTION.get(direction)).isConnected()))
        {
            return state.with(DIR_TO_CONNECTION.get(direction), PipeConnectionType.SIDE);
        }

        return state.with(DIR_TO_CONNECTION.get(direction), PipeConnectionType.NONE);
    }

    public abstract boolean canConnectTo(BlockState toState, Direction toFace, World world, BlockPos toPos);

    protected BlockState getConnectedState(BlockView world, BlockState state, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            PipeConnectionType property = state.get(DIR_TO_CONNECTION.get(direction));
            if (property == PipeConnectionType.SIDE)
                continue;
            BlockPos adjPos = pos.offset(direction);
            BlockState adjState = world.getBlockState(adjPos);
            state = state.with(DIR_TO_CONNECTION.get(direction), canConnectTo(adjState, direction.getOpposite(), (World) world, adjPos) ? PipeConnectionType.SIDE : PipeConnectionType.NONE);
        }
        return state.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (hand == Hand.OFF_HAND || !player.getStackInHand(hand).isEmpty())
        {
            return ActionResult.PASS;
        }
        if (!world.isClient)
        {
            Direction direction = hit.getSide();
            if (player.isSneaking())
            {
                direction = direction.getOpposite();
            }

            Vec3d hitPos = hit.getPos();

            Direction changeDirection = getUseDirection(direction, pos, hitPos);
            boolean connected = state.get(DIR_TO_CONNECTION.get(changeDirection)) == PipeConnectionType.SIDE;
            BlockState newState = state.with(DIR_TO_CONNECTION.get(changeDirection), connected ? PipeConnectionType.FORCED : PipeConnectionType.SIDE);
            world.setBlockState(pos, newState);
            world.playSound(null, pos, connectionChangeSound(world, state, pos), SoundCategory.BLOCKS, 1, 1);
            onConnectionUpdate(world, state, newState, pos, player);

            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    public static Direction getUseDirection(Direction direction, BlockPos pos, Vec3d hitPos)
    {
        NMVec2f relative = NMMaths.removeAxis(direction.getAxis(), hitPos.subtract(pos.getX(), pos.getY(), pos.getZ()));

        Direction changeDirection = direction;
        if (!relative.isWithin(0.5f, 0.5f, 0.25f))
        {
            // X axis case
            if (relative.getY() > 0.75)
                changeDirection = Direction.SOUTH;
            if (relative.getY() < 0.25)
                changeDirection = Direction.NORTH;
            if (relative.getX() < 0.25)
                changeDirection = Direction.DOWN;
            if (relative.getX() > 0.75)
                changeDirection = Direction.UP;

            switch (direction.getAxis())
            {
                case Y -> changeDirection = changeDirection.rotateClockwise(Direction.Axis.Z);
                case Z -> changeDirection = NMMaths.swapDirections(changeDirection.rotateClockwise(Direction.Axis.Y));
            }
        }

        return changeDirection;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(
                NORTH_CONNECTION,
                EAST_CONNECTION,
                SOUTH_CONNECTION,
                WEST_CONNECTION,
                UP_CONNECTION,
                DOWN_CONNECTION,
                WATERLOGGED
        );
    }

    public void onConnectionUpdate(World world, BlockState state, BlockState newState, BlockPos pos, PlayerEntity entity)
    {

    }

    public SoundEvent connectionChangeSound(World world, BlockState state, BlockPos pos)
    {
        return NMSounds.WRENCH_CLICK;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBucketPlace(BlockState state, Fluid fluid)
    {
        return false;
    }
}
