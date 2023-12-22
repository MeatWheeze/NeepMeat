package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.blockentity.machine.LinearOscillatorBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LinearOscillatorBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public LinearOscillatorBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new LinearOscillatorBlockEntity(pos, state);
    }
}
