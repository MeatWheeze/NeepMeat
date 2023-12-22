package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseVertFacingBlock;
import com.neep.neepmeat.blockentity.machine.AgitatorBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AgitatorBlock extends BaseVertFacingBlock implements BlockEntityProvider
{
    public AgitatorBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new AgitatorBlockEntity(pos, state);
    }
}
