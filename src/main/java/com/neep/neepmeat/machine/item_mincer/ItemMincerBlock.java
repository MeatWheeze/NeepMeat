package com.neep.neepmeat.machine.item_mincer;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ItemMincerBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public ItemMincerBlock(String itemName, int loreLines, Settings settings)
    {
        super(itemName, 64, false, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ITEM_MINCER.instantiate(pos, state);
    }
}
