package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@SuppressWarnings("deprecation")
public class MixerBlock extends BaseBlock implements BlockEntityProvider
{
    public static final VoxelShape OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    public MixerBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return OUTLINE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ());
        return world.getBlockState(pos.up()).isAir() && world.isSpaceEmpty(box) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        // Just in case
        if (world.getBlockState(pos.up()).isAir())
        {
            world.setBlockState(pos.up(), NMBlocks.MIXER_TOP.getDefaultState());
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockState(pos.up()).isOf(NMBlocks.MIXER_TOP))
        {
            if (world.getBlockEntity(pos) instanceof MixerBlockEntity be)
            {
                be.dropItems();
            }
            world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
    {
        return 1;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MixerBlockEntity(pos, state);
    }

}