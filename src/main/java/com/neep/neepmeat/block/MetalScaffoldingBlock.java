package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseBlock;
import com.neep.neepmeat.block.base.BaseSlabBlock;
import com.neep.neepmeat.block.base.BaseStairsBlock;
import com.neep.neepmeat.block.base.NMBlock;
import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.fluid_util.FluidNetwork;
import com.neep.neepmeat.fluid_util.NMFluidNetwork;
import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.item.BaseBlockItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MetalScaffoldingBlock extends BaseBlock implements NMBlock, Waterloggable
{
    private final String registryName;

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = Properties.BOTTOM;
    public final NMBlock stairs;
    public final NMBlock slab;

    public MetalScaffoldingBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());

        stairs = new BaseStairsBlock(this.getDefaultState(),registryName + "_stairs", itemMaxStack, settings);
        BlockInitialiser.BLOCKS.put(stairs.getRegistryName(), stairs);

        slab = new BaseSlabBlock(this.getDefaultState(),registryName + "_slab", itemMaxStack, settings);
        BlockInitialiser.BLOCKS.put(slab.getRegistryName(), slab);

        this.registryName = registryName;
        this.setDefaultState((this.stateManager.getDefaultState()).with(WATERLOGGED, false).with(BOTTOM, false));
        BlockInitialiser.BLOCKS.put(getRegistryName(), this);
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
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!world.isClient())
        {
            world.getBlockTickScheduler().schedule(pos, this, 1);
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
}
