package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseHorFacingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;

public class VatControllerBlock extends BaseHorFacingBlock implements IVatStructure
{
    public VatControllerBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return context.getPlayerLookDirection().getAxis().isVertical() ? getDefaultState() :
                this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
    }
}
