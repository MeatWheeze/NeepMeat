package com.neep.neepmeat.machine.power_flower;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PowerFlowerSeedsBlock extends BaseBlock
{
    private final VoxelShape shape = Block.createCuboidShape(0, 0, 0, 16, 5, 16);

    public PowerFlowerSeedsBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
    }

    @Override
    public TagKey<Block> getPreferredTool()
    {
        return BlockTags.HOE_MINEABLE;
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context)
    {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return shape;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return validBlock(world.getBlockState(pos.down()).getBlock()) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private boolean validBlock(Block state)
    {
        return state.getDefaultState().isIn(BlockTags.DIRT);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return super.getPlacementState(ctx);
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        grow(world, pos, state, random);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
//        if (!world.isClient())
//        {
//            grow(world, pos, state, world.getRandom());
//        }
        return ActionResult.SUCCESS;
    }

    public void grow(World world, BlockPos pos, BlockState state, Random random)
    {
        Direction[] horizontal = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        boolean controller = false;

        world.setBlockState(pos, NMBlocks.POWER_FLOWER_GROWTH.getDefaultState());

        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction d : horizontal)
        {
            mutable.set(pos, d);
            for (int i = 0; i < 2; ++i)
            {
                BlockState nextState = world.getBlockState(mutable);
                if (nextState.isAir() || nextState.isOf(NMBlocks.POWER_FLOWER_SEEDS))
                    world.setBlockState(mutable, NMBlocks.POWER_FLOWER_GROWTH.getDefaultState());

                Direction direction = horizontal[random.nextInt(4)];
                mutable.set(mutable, direction);
            }
        }

        int height = Math.max(4, random.nextInt(6));
        for (int j = 0; j < height; ++j)
        {
            BlockPos stem = mutable.set(pos, 0, j, 0).toImmutable();
            BlockState nextState = world.getBlockState(stem);
            if (canGrowInto(nextState))
            {
                world.setBlockState(stem, NMBlocks.POWER_FLOWER_GROWTH.getState(world, stem));
            }

            for (Direction d : horizontal)
            {
                mutable.set(stem, d);
                if (random.nextFloat() > ((float) j / height))
                {
                    BlockState thing = world.getBlockState(mutable);
                    if (canGrowInto(thing) && !world.getBlockState(mutable.down()).isAir())
                    {
                        if (!controller && random.nextFloat() < 0.4)
                        {
                            world.setBlockState(mutable, NMBlocks.POWER_FLOWER_CONTROLLER.getDefaultState());
                            controller = true;
                        }
                        else
                        {
                            world.setBlockState(mutable, NMBlocks.POWER_FLOWER_GROWTH.getState(world, mutable));
                        }
                    }
                }
            }
        }
    }

    public static boolean canGrowInto(BlockState state)
    {
        return state.isAir() || state.isOf(NMBlocks.POWER_FLOWER_SEEDS);
    }
}
