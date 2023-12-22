package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.block.IMeatBlock;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MixerTopBlock extends Block implements IMeatBlock, BlockEntityProvider
{
    private final String registryName;

    public MixerTopBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings.nonOpaque());
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MixerTopBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockState(pos.down()).isOf(NMBlocks.MIXER))
        {
            world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return NMBlocks.MIXER.getPickStack(world, pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }
}
