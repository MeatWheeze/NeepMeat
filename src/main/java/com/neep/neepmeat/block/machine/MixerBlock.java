package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseColumnBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;

public class MixerBlock extends BaseColumnBlock
{
    public MixerBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(AXIS, ctx.getPlayerFacing().getAxis());
    }
}