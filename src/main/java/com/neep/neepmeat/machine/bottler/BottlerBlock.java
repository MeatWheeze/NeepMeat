package com.neep.neepmeat.machine.bottler;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BottlerBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public BottlerBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.BOTTLER.instantiate(pos, state);
    }
}
