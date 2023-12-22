package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.block.multiblock.IMultiBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class VatCasingBlock extends BaseBlock implements IVatComponent, BlockEntityProvider
{
    public VatCasingBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
//        return NMBlockEntities.VAT_CASING.instantiate(pos, state);
//        return new IMultiBlock.Entity(NMBlockEntities.VAT_CASING, pos, state);
        return null;
    }
}
