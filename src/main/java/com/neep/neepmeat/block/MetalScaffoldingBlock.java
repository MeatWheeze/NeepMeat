package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.block.BaseSlabBlock;
import com.neep.meatlib.block.BaseStairsBlock;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.function.Consumer;

public class MetalScaffoldingBlock extends BaseBlock implements MeatlibBlock, Waterloggable
{
    private final String registryName;

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = Properties.BOTTOM;
    public final MeatlibBlock stairs;
    public final MeatlibBlock slab;

    public MetalScaffoldingBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());

        stairs = new BaseStairsBlock(this.getDefaultState(),registryName + "_stairs", itemSettings, settings);
        BlockRegistry.queue(stairs);

        slab = new BaseSlabBlock(this.getDefaultState(),registryName + "_slab", itemSettings, settings);
        BlockRegistry.queue(slab);

        this.registryName = registryName;
        this.setDefaultState((this.stateManager.getDefaultState()).with(WATERLOGGED, false).with(BOTTOM, false));
        BlockRegistry.queue(this);
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        return (this.getDefaultState()
                .with(WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER))
                .with(BOTTOM, this.shouldBeBottom(world, blockPos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        state = state.with(BOTTOM, world.getBlockState(pos.up()).getBlock() instanceof MetalScaffoldingBlock);

        if (state.get(WATERLOGGED))
        {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!world.isClient())
        {
            world.scheduleBlockTick(pos, this, 1);
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED, BOTTOM);
    }

    private boolean shouldBeBottom(BlockView world, BlockPos pos)
    {
        return world.getBlockState(pos.down()).isOf(this);
    }

    public void generateRecipe(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerStairsRecipe(exporter, stairs, this);
        MeatRecipeProvider.offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, slab, this);
    }
}
