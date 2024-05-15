package com.neep.neepmeat.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.interfaces.AbstractMinecartEntityAccess;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Vector;


public class PlayerControlTrack extends BaseRailBlock implements BlockEntityProvider
{
    public static final EnumProperty<RailShape> RAIL_SHAPE_NO_SLOPE = EnumProperty.of(
            "shape",
            RailShape.class,
            shape -> !shape.isAscending() && (shape != RailShape.NORTH_EAST && shape != RailShape.NORTH_WEST && shape != RailShape.SOUTH_EAST && shape != RailShape.SOUTH_WEST)
    );

//    public static final EnumProperty<AxialDirection> FACING = EnumProperty.of("direction", AxialDirection.class);
    public static final EnumProperty<RailShape> SHAPE = RAIL_SHAPE_NO_SLOPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public PlayerControlTrack(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(true, settings, registryName);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH)
                .with(POWERED, false)
                .with(WATERLOGGED, false));
        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));
    }

    @Override
    public Property<RailShape> getShapeProperty()
    {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(SHAPE, POWERED, WATERLOGGED);
    }

    @Override
    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor)
    {
        boolean powered = state.get(POWERED);
        boolean receiving = world.isReceivingRedstonePower(pos);
        if (receiving != powered)
        {
            BlockState newState = state.with(POWERED, receiving);
            world.setBlockState(pos, state.with(POWERED, receiving), Block.NOTIFY_ALL);
            world.getBlockEntity(pos, NMBlockEntities.CONTROL_TRACK).ifPresent(be ->
            {
                be.setCachedState(newState);
            });
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean waterlogged = fluidState.getFluid() == Fluids.WATER;
        BlockState blockState = super.getDefaultState();
        Direction direction = ctx.getHorizontalPlayerFacing();
        boolean eastWest = direction == Direction.EAST || direction == Direction.WEST;
        return blockState
                .with(this.getShapeProperty(), eastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)
//                .with(FACING, AxialDirection.from(direction))
                .with(WATERLOGGED, waterlogged);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CONTROL_TRACK.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.CONTROL_TRACK, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }

    public static class TrackBlockEntity extends BlockEntity
    {
        public TrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public void serverTick()
        {
//            boolean powered = getCachedState().get(POWERED);
////            AxialDirection direction = getCachedState().get(FACING);
//            RailShape railShape = getCachedState().get(SHAPE);
//
            Box box = new Box(getPos());

            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            world.getNonSpectatingEntities(AbstractMinecartEntity.class, box).forEach(minecart ->
            {
                RailShape shape = world.getBlockState(pos).get(RAIL_SHAPE_NO_SLOPE);
                boolean playerPassenger = minecart.getFirstPassenger() instanceof PlayerEntity;
                Vec3d controlVel = minecart.getVelocity();
                if (playerPassenger)
                {
                    Vec3d playerVel = ((AbstractMinecartEntityAccess) minecart).neepmeat$getControllerVelocity();
                    if (playerVel.horizontalLengthSquared() > 0)
                    {
                        controlVel = playerVel;
                    }
                }

                if (controlVel.horizontalLengthSquared() > 0)
                {
                    Direction direction = Direction.getFacing(controlVel.x, 0, controlVel.z);
                    if (direction == Direction.NORTH || direction == Direction.SOUTH)
                    {
                        if (shape != RailShape.NORTH_SOUTH)
                            world.setBlockState(pos, getCachedState().with(RAIL_SHAPE_NO_SLOPE, RailShape.NORTH_SOUTH));
                    }
                    else
                    {
                        if (shape != RailShape.EAST_WEST)
                            world.setBlockState(pos, getCachedState().with(RAIL_SHAPE_NO_SLOPE, RailShape.EAST_WEST));
                    }

                    Vector3f unit = direction.getUnitVector().mul(0.3f);
                    minecart.addVelocity(unit.x, unit.y, unit.z);
                }
            });
        }

        protected static Direction.Axis axis(RailShape railShape)
        {
            return switch (railShape)
            {
                case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> Direction.Axis.Z;
                case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> Direction.Axis.X;
                default -> throw new IllegalStateException();
            };
        }
    }
}
