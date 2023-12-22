package com.neep.neepmeat.machine.breaker;

import com.google.common.collect.ImmutableMap;
import com.neep.meatlib.block.BaseFacingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class LinearOscillatorBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 5, 16, 16 ,16);
    public static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0, 0, 0, 11, 16 ,16);
    public static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16 ,11);
    public static final VoxelShape WEST_SHAPE = Block.createCuboidShape(5, 0, 0, 16, 16 ,16);
    public static final VoxelShape UP_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 11 ,16);
    public static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0, 5, 0, 16, 16 ,16);

    public static final Map<Direction, VoxelShape> SHAPES = (new ImmutableMap.Builder<Direction, VoxelShape>())
            .put(Direction.NORTH, NORTH_SHAPE)
            .put(Direction.EAST, EAST_SHAPE)
            .put(Direction.SOUTH, SOUTH_SHAPE)
            .put(Direction.WEST, WEST_SHAPE)
            .put(Direction.UP, UP_SHAPE)
            .put(Direction.DOWN, DOWN_SHAPE)
            .build();

    public LinearOscillatorBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
//        List<VoxelShape> shapes = List.of(NORTH_SHAPE, EAST_SHAPE, SOUTH_SHAPE, WEST_SHAPE, UP_SHAPE, DOWN_SHAPE);
//        SHAPES = Arrays.stream(Direction.values()).map((direction -> shapes.get(direction.getId()))).;
    }

    public VoxelShape getShapeForState(BlockState state)
    {
        return SHAPES.get(state.get(FACING));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return getShapeForState(state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof LinearOscillatorBlockEntity be && be.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof LinearOscillatorBlockEntity be && !world.isClient())
        {
//            be.update((ServerWorld) world, pos, fromPos, state);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new LinearOscillatorBlockEntity(pos, state);
    }

//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
//    {
//        return MiscUitls.checkType(type, NMBlockEntities.LINEAR_OSCILLATOR, LinearOscillatorBlockEntity::serverTick, world);
//    }
}
