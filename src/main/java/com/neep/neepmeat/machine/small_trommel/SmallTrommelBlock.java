package com.neep.neepmeat.machine.small_trommel;

import com.neep.meatlib.block.BaseDummyBlock;
import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SmallTrommelBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public SmallTrommelBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.SMALL_TROMMEL.instantiate(pos, state);
    }

    public static void createStructure(World world, BlockPos pos, Direction facing)
    {
        world.setBlockState(pos, NMBlocks.SMALL_TROMMEL_STRUCTURE.getDefaultState().with(FACING, facing), Block.NOTIFY_LISTENERS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        BlockPos offsetPos = pos.offset(state.get(FACING));
        return super.canPlaceAt(state, world, pos) && world.isAir(offsetPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        Direction facing = state.get(FACING);
        BlockPos facingPos = pos.offset(facing);
        if (world.isAir(facingPos))
        {
            createStructure(world, facingPos, facing);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);
        Direction facing = state.get(FACING);
        BlockPos facingPos = pos.offset(facing);
        if (world.getBlockState(facingPos).isOf(NMBlocks.SMALL_TROMMEL_STRUCTURE))
        {
            world.setBlockState(facingPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
    }

    public static class Structure extends BaseDummyBlock
    {
        public Structure(String registryName, Settings settings)
        {
            super(registryName, settings.nonOpaque());
            this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        }

        @Override
        public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
        {
            return 1.0f;
        }

        @Override
        public BlockRenderType getRenderType(BlockState state)
        {
            return BlockRenderType.INVISIBLE;
        }

        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
        {
            Direction facing = state.get(FACING);
            BlockPos offsetPos = pos.offset(facing.getOpposite());
            if (world.getBlockState(offsetPos).isOf(NMBlocks.SMALL_TROMMEL))
            {
                world.setBlockState(offsetPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
        {
            super.appendProperties(builder);
            builder.add(FACING);
        }

        @Override
        public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
        {
            return NMBlocks.SMALL_TROMMEL.getPickStack(world, pos, state);
        }

        public static Storage<FluidVariant> getFluidStorage(World world, BlockPos pos, BlockState state, @Nullable BlockEntity entity, Direction direction)
        {
            Direction facing = state.get(FACING);
            BlockPos offsetPos = pos.offset(facing.getOpposite());
            if (direction == Direction.DOWN && world.getBlockEntity(offsetPos) instanceof SmallTrommelBlockEntity be)
            {
                return be.getOutputFluidStorage();
            }
            return null;
        }

        public static Storage<ItemVariant> getItemStorage(World world, BlockPos pos, BlockState state, @Nullable BlockEntity entity, Direction direction)
        {
            return null;
        }
    }
}
