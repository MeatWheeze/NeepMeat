package com.neep.neepmeat.machine.death_blades;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DeathBladesBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, Block.createCuboidShape(4, 4, 4, 12, 12, 16),
            Direction.EAST, Block.createCuboidShape(0, 4, 4, 12, 12, 12),
            Direction.SOUTH, Block.createCuboidShape(4, 4, 0, 12, 12, 12),
            Direction.WEST, Block.createCuboidShape(4, 4, 4, 16, 12, 12),
            Direction.UP, Block.createCuboidShape(4, 0, 4, 12, 12, 12),
            Direction.DOWN, Block.createCuboidShape(4, 4, 4, 12, 16, 12)
    );

    public DeathBladesBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.DEATH_BLADES, null, DeathBladesBlockEntity::clientTick, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.DEATH_BLADES.instantiate(pos, state);
    }
}
