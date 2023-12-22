package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseBlock;
import com.neep.neepmeat.blockentity.ItemDuctBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ItemDuctBlock extends BaseBlock implements BlockEntityProvider
{
    public ItemDuctBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new ItemDuctBlockEntity(pos, state);
    }
}
