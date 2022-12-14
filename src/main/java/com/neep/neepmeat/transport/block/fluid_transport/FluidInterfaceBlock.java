package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.Maps;
import com.neep.meatlib.block.BaseFacingBlock;
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

//    private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
//            Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 15.75, 1),
//            Direction.SOUTH, Block.createCuboidShape(3, 0, 15, 13, 15.75, 16),
//            Direction.WEST, Block.createCuboidShape(0, 0, 3, 1, 15.75, 13),
//            Direction.EAST, Block.createCuboidShape(15, 0, 3, 16, 15.75, 13)));

    public FluidInterfaceBlock(String itemName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, factory, settings.nonOpaque());
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
