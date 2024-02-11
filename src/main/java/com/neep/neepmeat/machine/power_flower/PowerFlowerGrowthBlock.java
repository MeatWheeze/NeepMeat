package com.neep.neepmeat.machine.power_flower;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PowerFlowerGrowthBlock extends BaseBlock
{
    public static final IntProperty GROWTH = Properties.AGE_1;

    private final VoxelShape fullShape = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    private final VoxelShape topShape = Block.createCuboidShape(0, 0, 0, 16, 14, 16);

    public PowerFlowerGrowthBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
        this.setDefaultState(getDefaultState().with(GROWTH, 0));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context)
    {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return state.get(GROWTH) == 0 ? fullShape : topShape;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
//        return validBlock(world.getBlockState(pos.down()).getBlock()) && super.canPlaceAt(state, world, pos);
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
//        if (state.get(GROWTH) == 0 && sourcePos.equals(pos.down()) && !validBlock(world.getBlockState(sourcePos).getBlock()))
//        {
//            world.breakBlock(pos, true);
//        }
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
        return super.getPlacementState(ctx).with(GROWTH, 0);
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
//        return state.get(GROWTH) == 0;
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
//        if (state.get(GROWTH) == 0 && validBlock(world.getBlockState(pos.down()).getBlock()))
//        {
//            world.setBlockState(pos.down(), state.with(GROWTH, 1));
//            world.setBlockState(pos, Blocks.AIR.getDefaultState());
//        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(GROWTH);
    }
}
