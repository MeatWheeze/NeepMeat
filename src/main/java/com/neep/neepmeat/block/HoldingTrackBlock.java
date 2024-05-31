package com.neep.neepmeat.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.AxialDirection;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.RailShape;
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
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class HoldingTrackBlock extends BaseRailBlock implements BlockEntityProvider
{
    public static final EnumProperty<AxialDirection> FACING = EnumProperty.of("direction", AxialDirection.class);
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public HoldingTrackBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(true, settings, registryName);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, false).with(WATERLOGGED, false).with(FACING, AxialDirection.POSITIVE));
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
        builder.add(SHAPE, POWERED, FACING, WATERLOGGED);
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
            world.getBlockEntity(pos, NMBlockEntities.HOLDING_TRACK).ifPresent(be ->
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
        Direction direction = ctx.getPlayerFacing();
        boolean eastWest = direction == Direction.EAST || direction == Direction.WEST;
        return blockState
                .with(this.getShapeProperty(), eastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)
                .with(FACING, AxialDirection.from(direction))
                .with(WATERLOGGED, waterlogged);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.HOLDING_TRACK.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.HOLDING_TRACK, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }

    public static class HoldingTrackBlockEntity extends BlockEntity
    {
        public HoldingTrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public void serverTick()
        {
            boolean powered = getCachedState().get(POWERED);
            AxialDirection direction = getCachedState().get(FACING);
            RailShape railShape = getCachedState().get(SHAPE);

            Box box = new Box(getPos()).shrink(0.24, 0.24, 0.24);

            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            List<AbstractMinecartEntity> result = world.getNonSpectatingEntities(AbstractMinecartEntity.class, box);
            if (!result.isEmpty())
            {
                AbstractMinecartEntity e = result.get(0);

                if (!powered)
                {
                    e.setPos(x, y, z);
                    e.setVelocity(0, 0, 0);
                }
                else
                {
                    Vec3f vel = direction.with(axis(railShape));
                    e.setVelocity(vel.getX(), vel.getY(), vel.getZ());
                }
            }
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
