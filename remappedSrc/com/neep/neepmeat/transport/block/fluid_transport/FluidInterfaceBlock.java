package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.Maps;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.transport.machine.fluid.FluidInterfaceBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemDuctBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidInterfaceBlock extends BaseFacingBlock implements BlockEntityProvider
{
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public FluidInterfaceBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
            return this.getDefaultState().with(FACING, context.getPlayer().isSneaking() ? context.getSide() : context.getSide().getOpposite());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof ItemDuctBlockEntity be)
        {
            be.updateApiCache(pos, state);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.getBlockEntity(pos) instanceof ItemDuctBlockEntity be)
        {
            be.updateApiCache(pos, state);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FluidInterfaceBlockEntity(pos, state);
    }
}
