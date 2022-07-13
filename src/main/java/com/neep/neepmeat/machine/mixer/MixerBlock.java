package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MixerBlock extends BaseBlock
{
    public MixerBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {

        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ());
        return world.getBlockState(pos.up()).isAir() && world.isSpaceEmpty(box);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        // Just in case
        if (world.getBlockState(pos.up()).isAir())
        {
            world.setBlockState(pos.up(), NMBlocks.MIXER_TOP.getDefaultState());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockState(pos.up()).isOf(NMBlocks.MIXER_TOP))
        {
            world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        }
    }
}