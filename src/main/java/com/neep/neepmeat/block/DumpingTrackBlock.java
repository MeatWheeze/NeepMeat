package com.neep.neepmeat.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;


public class DumpingTrackBlock extends BaseRailBlock implements SpecialRail
{
    public static final EnumProperty<RailShape> RAIL_SHAPE_NO_SLOPE = EnumProperty.of(
            "shape",
            RailShape.class,
            shape -> (shape != RailShape.NORTH_EAST && shape != RailShape.NORTH_WEST && shape != RailShape.SOUTH_EAST && shape != RailShape.SOUTH_WEST)
    );

    public static final EnumProperty<RailShape> SHAPE = RAIL_SHAPE_NO_SLOPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public DumpingTrackBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(true, settings, registryName);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH)
                .with(POWERED, false)
                .with(WATERLOGGED, false));
        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        if (!world.isClient && world.getBlockState(pos).isOf(this))
        {
            this.updateBlockState(state, world, pos, sourceBlock);
        }
    }

    @Override
    public void apply(World world, BlockPos pos, BlockState state, AbstractMinecartEntity minecart)
    {
        if (world.isClient() || !state.get(POWERED))
            return;

        Entity passenger = minecart.getFirstPassenger();
        double thisX = pos.getX() + 0.5;
        double thisZ = pos.getZ() + 0.5;
        if (passenger != null
                && Math.abs(minecart.getX() - thisX) < 0.5
                && Math.abs(minecart.getZ() - thisZ) < 0.5)
        {
            passenger.stopRiding();
            Vec3d prevPassengerPos = passenger.getPos();
            passenger.setPosition(thisX, prevPassengerPos.y, thisZ);
        }
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
            if (powered)
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1f, 0.7f);
            else
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1f, 0.7f);

            BlockState newState = state.with(POWERED, receiving);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
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
                .with(WATERLOGGED, waterlogged);
    }
}
