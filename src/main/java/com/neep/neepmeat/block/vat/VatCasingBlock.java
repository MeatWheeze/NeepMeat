package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;

public class VatCasingBlock extends BaseBlock implements IVatStructure
{
    public VatCasingBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

}
