package com.neep.neepmeat.machine.assembler;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.block.IMeatBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AssemblerBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public AssemblerBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return Block.createCuboidShape(0, 0, 0, 16, 32, 16);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof AssemblerBlockEntity be)
        {
            player.openHandledScreen(be);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof AssemblerBlockEntity be)
        {
            be.update();
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.ASSEMBLER, AssemblerBlockEntity::serverTick, world);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        super.onBlockAdded(state, world, pos, oldState, notify);
        BlockPos up = pos.up();
        if (world.getBlockState(up).isAir())
        {
            world.setBlockState(up, NMBlocks.ASSEMBLER_TOP.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        BlockPos up = pos.up();
        if (world.getBlockState(up).isOf(NMBlocks.ASSEMBLER_TOP) && !newState.isOf(this))
        {
            world.setBlockState(up, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
        if (world.getBlockEntity(pos) instanceof AssemblerBlockEntity be)
        {
            scatterItems(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, be.storage.getInventory());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static void scatterItems(World world, double x, double y, double z, Inventory inventory)
    {
        for (int i = 12; i < inventory.size(); ++i)
        {
            ItemScatterer.spawn(world, x, y, z, inventory.getStack(i));
        }
    }

    @Override
    public void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
    {
        super.spawnBreakParticles(world, player, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ASSEMBLER.instantiate(pos, state);
    }

    public static class Top extends Block implements IMeatBlock
    {
        protected final String registryName;

        public Top(String registryName, Settings settings)
        {
            super(settings);
            this.registryName = registryName;
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
        {
            return Block.createCuboidShape(0, -32, 0, 16, 16, 16);
        }

        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
        {
            BlockPos down = pos.down();
            if (world.getBlockState(down).getBlock() instanceof AssemblerBlock block)
            {
                BlockState downState = world.getBlockState(down);
                return block.onUse(downState, world, down, player, hand, hit);
            }
            return super.onUse(state, world, pos, player, hand, hit);
        }

        @Override
        public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
        {
            return 1.0f;
        }

        @Override
        public PistonBehavior getPistonBehavior(BlockState state)
        {
            return PistonBehavior.IGNORE;
        }

        @Override
        protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
        {
            ((AssemblerBlock) NMBlocks.ASSEMBLER).spawnBreakParticles(world, player, pos, state);
        }

        @Override
        public BlockRenderType getRenderType(BlockState state)
        {
            return BlockRenderType.INVISIBLE;
        }

        @Override
        public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
        {
            return NMBlocks.ASSEMBLER.getPickStack(world, pos, state);
        }

        @Override
        public String getRegistryName()
        {
            return registryName;
        }

        @Override
        public ItemConvertible dropsLike()
        {
            return NMBlocks.ASSEMBLER;
        }

        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
        {
            super.onStateReplaced(state, world, pos, newState, moved);
            BlockPos down = pos.down();
            if (world.getBlockState(down).isOf(NMBlocks.ASSEMBLER) && !newState.isOf(this))
            {
                world.setBlockState(down, Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            }
        }
    }
}
