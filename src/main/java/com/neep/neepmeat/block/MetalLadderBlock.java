package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MetalLadderBlock extends LadderBlock implements MeatlibBlock
{
    public static final BooleanProperty TOP = BooleanProperty.of("top");

    public MetalLadderBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        ctx.append(this, itemSettings.create(this, ctx, itemSettings));
        setDefaultState(getStateManager().getDefaultState().with(TOP, false));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return true;
    }

    private BlockState getState(WorldAccess world, BlockPos pos, BlockState state)
    {
        Direction facing = state.get(FACING);
        boolean ladderAbove = world.getBlockState(pos.up()).getBlock() instanceof MetalLadderBlock;
        BlockPos offset = pos.offset(facing.getOpposite());
        boolean solidUpFace = world.getBlockState(offset).isSideSolidFullSquare(world, offset, Direction.UP);

        return state.with(TOP, !ladderAbove && solidUpFace);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        if (!ctx.canReplaceExisting())
        {
            BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(ctx.getSide().getOpposite()));
            if (blockState.isOf(this) && blockState.get(FACING) == ctx.getSide())
            {
                return null;
            }
        }

        WorldAccess world = ctx.getWorld();
        FluidState fluidState = world.getFluidState(ctx.getBlockPos());

        BlockState blockState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

        return getState(world, ctx.getBlockPos(), blockState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        return getState(world, pos, super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(TOP);
    }
}
