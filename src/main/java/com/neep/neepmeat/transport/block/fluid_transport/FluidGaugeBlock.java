package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.ImmutableMap;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidGaugeBlockEntity;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidGaugeBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 7);

    protected final Map<Direction, VoxelShape> SHAPE_MAP = ImmutableMap.of(
            Direction.NORTH, Block.createCuboidShape(6, 0, 0, 10, 16, 2),
            Direction.EAST, Block.createCuboidShape(14, 0, 6, 16, 16, 10),
            Direction.SOUTH, Block.createCuboidShape(6, 0, 14, 10, 16, 16),
            Direction.WEST, Block.createCuboidShape(0, 0, 6, 2, 16, 10),
            Direction.UP, Block.createCuboidShape(6, 14, 0, 10, 16, 16),
            Direction.DOWN, Block.createCuboidShape(6, 0, 0, 10, 2, 16)
    );

    public FluidGaugeBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getSide().getOpposite());
    }

    public static BlockState getLevelState(BlockState old, int comparatorLevel)
    {
        int level = comparatorLevel > 0 && comparatorLevel <= 2 ? 1 : (int) MathHelper.clamp(comparatorLevel / 15f * 7, 0, 7);
        return old.with(LEVEL, level);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE_MAP.get(state.get(FACING));
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        if (world.getBlockEntity(pos) instanceof FluidGaugeBlockEntity be)
        {
            return be.getOutput();
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state)
    {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FLUID_GAUGE.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.FLUID_GAUGE, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
