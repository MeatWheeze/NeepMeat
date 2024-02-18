package com.neep.neepmeat.machine.pedestal;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.util.ItemUtils;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BaseBlock implements BlockEntityProvider, DataCable
{
    public PedestalBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random)
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
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity be && !world.isClient())
        {
            onEntityCollided(world, pos, state, entity, be);
        }
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
        Random random = Random.create();
        for (int i = 0; i <= count; ++i)
        {
            world.addParticle(new SwirlingParticleEffect(NMParticles.BLOCK_SWIRL,
                    Blocks.AMETHYST_BLOCK.getDefaultState(), 0.4, speed), pos.getX() + 0.5, pos.getY() + 0.1 + random.nextFloat(), pos.getZ() + 0.5, 0, dy, 0.1);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.PEDESTAL, (world1, pos, state1, blockEntity) -> blockEntity.tick(), null, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.PEDESTAL.instantiate(pos, state);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity be)
        {
            return be.storage.amount == 1 ? 15 : 0;
        }
        return 0;
    }

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, PedestalBlockEntity be)
    {
        if (!world.isClient && entity instanceof ItemEntity item)
        {
            be.extractFromItem(item);
        }
    }
}