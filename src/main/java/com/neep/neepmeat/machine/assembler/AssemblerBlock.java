package com.neep.neepmeat.machine.assembler;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.FluidPipe;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class AssemblerBlock extends TallBlock implements BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public AssemblerBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return Block.createCuboidShape(0, 0, 0, 16, 32, 16);
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new Top(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).getItem() instanceof BlockItem bi)
        {
            if (bi.getBlock() instanceof ItemPipe || bi.getBlock() instanceof FluidPipe) return ActionResult.PASS;
        }

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
        return MiscUtils.checkType(type, NMBlockEntities.ASSEMBLER, AssemblerBlockEntity::serverTick, null, world);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return super.canPlaceAt(state, world, pos) && world.getBlockState(pos.up()).isAir();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ASSEMBLER.instantiate(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    public class Top extends TallBlock.Structure
    {
        public Top(String registryName, Settings settings)
        {
            super(registryName, settings);
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
        public BlockRenderType getRenderType(BlockState state)
        {
            return BlockRenderType.INVISIBLE;
        }
    }
}
