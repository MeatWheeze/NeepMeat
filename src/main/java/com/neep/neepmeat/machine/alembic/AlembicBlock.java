package com.neep.neepmeat.machine.alembic;

import com.neep.meatlib.block.BaseHorFacingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;

public class AlembicBlock extends BaseHorFacingBlock
{
    public AlembicBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }
}
