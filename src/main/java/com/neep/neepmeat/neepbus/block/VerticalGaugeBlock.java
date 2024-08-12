package com.neep.neepmeat.neepbus.block;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.neepbus.NeepBusBlocks;
import com.neep.neepmeat.neepbus.NeepBusPort;
import com.neep.neepmeat.neepbus.NeepBusProvider;
import com.neep.neepmeat.neepbus.SimpleScreenHandlerFactory;
import com.neep.neepmeat.neepbus.block.entity.VerticalGaugeBlockEntity;
import com.neep.neepmeat.neepbus.screen.NeepBusScreenHandler;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class VerticalGaugeBlock extends BaseHorFacingBlock implements BlockEntityProvider, DataCable, NeepBusProvider
{
    public static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(4, 1, 14, 12, 15, 16);
    public static final VoxelShape EAST_SHAPE = MiscUtil.rotateShapeY(NORTH_SHAPE, 90);
    public static final VoxelShape SOUTH_SHAPE = MiscUtil.rotateShapeY(NORTH_SHAPE, 180);
    public static final VoxelShape WEST_SHAPE = MiscUtil.rotateShapeY(NORTH_SHAPE, 270);

    public VerticalGaugeBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, itemSettings, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NeepBusBlocks.VERTICAL_GAUGE_BE.instantiate(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        for (Direction direction : ctx.getPlacementDirections())
        {
            BlockState blockState = getDefaultState();
            if (direction.getAxis() == Direction.Axis.Y)
            {
                blockState = blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
            }
            else
            {
                blockState = blockState.with(FACING, direction.getOpposite());
            }

            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos()))
            {
                return blockState;
            }
        }
        return getDefaultState();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {

        return switch (state.get(FACING))
        {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof VerticalGaugeBlockEntity be)
        {
            if (player.isSneaking())
            {
                player.openHandledScreen(NeepBusScreenHandler.getFactory(be.getConfig()));
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof VerticalGaugeBlockEntity be)
        {
            return be.getConfig().getInputPorts();
        }

        return NO_PORTS;
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {

    }
}
