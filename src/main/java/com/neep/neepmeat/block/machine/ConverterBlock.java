package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.block.IMeatBlock;
import com.neep.neepmeat.blockentity.ConverterBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.util.MiscUitls;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ConverterBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public ConverterBlock(String registryName, int itemMaxStack, boolean hasLore, FabricBlockSettings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.isSneaking())
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
//        BlockPos basePos = pos.offset(state.get(FACING).getOpposite());
//        return world.getBlockState(basePos).isAir();
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
//        Direction facing = state.get(FACING).getOpposite();
//        BlockPos basePos = pos.offset(facing);
//        if (world.getBlockState(basePos).isAir())
//        {
//            world.setBlockState(basePos, NMBlocks.CONVERTER_BASE.getDefaultState()
//                    .with(Base.FACING, facing));
//        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
//        Direction look = context.getPlayerLookDirection();
//        return look.getAxis().isVertical() ? getDefaultState() : getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
//        System.out.println("place");
//        return  this.getDefaultState();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);
//        if (!newState.isOf(this))
//        {
//            BlockPos basePos = pos.offset(state.get(FACING).getOpposite());
//            if (world.getBlockState(basePos).isOf(NMBlocks.CONVERTER_BASE))
//            {
//                world.setBlockState(basePos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
//            }
//        }
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
//        builder.add(TYPE);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUitls.checkType(type, NMBlockEntities.CONVERTER, ConverterBlockEntity::serverTick, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CONVERTER.instantiate(pos, state);
    }

    public static class Base extends HorizontalFacingBlock implements BlockEntityProvider, IMeatBlock
    {

        protected String registryName;

        public Base(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
        {
            super(settings);
            this.registryName = registryName;
        }

        @Nullable
        @Override
        public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
        {
            return NMBlockEntities.CONVERTER_BASE.instantiate(pos, state);
        }

        @Override
        public BlockRenderType getRenderType(BlockState state)
        {
            return BlockRenderType.INVISIBLE;
        }

        @Override
        public String getRegistryName()
        {
            return registryName;
        }

        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
        {
            super.onStateReplaced(state, world, pos, newState, moved);
//            if (!newState.isOf(this))
//            {
//                BlockPos mainPos = pos.offset(state.get(FACING).getOpposite());
//                if (world.getBlockState(mainPos).isOf(NMBlocks.CONVERTER))
//                {
//                    world.setBlockState(mainPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
//                }
//            }
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
        {
            builder.add(FACING);
        }
    }
}