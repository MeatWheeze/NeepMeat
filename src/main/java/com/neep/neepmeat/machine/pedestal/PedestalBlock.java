package com.neep.neepmeat.machine.pedestal;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.api.block.pipe.IDataCable;
import com.neep.neepmeat.blockentity.DisplayPlatformBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import com.neep.neepmeat.storage.WritableStackStorage;
import com.neep.neepmeat.util.ItemUtils;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.mixin.registry.sync.AccessorRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PedestalBlock extends BaseBlock implements BlockEntityProvider, IDataCable
{
    public PedestalBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return Block.createCuboidShape(0, 0, 0, 16, 11, 16);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity be && !world.isClient())
        {
            be.update(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        super.scheduledTick(state, world, pos, random);
        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity be)
        {
            be.recipeBehaviour.finishRecipe();
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity be && !world.isClient)
        {
            return ActionResult.success(WritableStackStorage.handleInteract(player, hand, be.storage));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity be)
        {
            ItemUtils.scatterItems(world, pos, be.storage);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        super.randomDisplayTick(state, world, pos, random);
        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity be && be.hasRecipe)
        {
//            be.recipeBehaviour.load(world);
//            if (be.recipeBehaviour.getRecipeId() != null)
            spawnParticles(world, pos.down(2), 5, 0.4, 0.1);
        }
    }

    public static void spawnParticles(World world, BlockPos pos, int count, double dy, double speed)
    {
        Random random = new Random(pos.asLong());
        for (int i = 0; i <= count; ++i)
        {
            world.addParticle(new SwirlingParticleEffect(NMParticles.BLOCK_SWIRL,
                    Blocks.AMETHYST_BLOCK.getDefaultState(), 0.4, speed), pos.getX() + 0.5, pos.getY() + 0.1 + random.nextFloat(), pos.getZ() + 0.5, 0, dy, 0.1);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.PEDESTAL.instantiate(pos, state);
    }
}