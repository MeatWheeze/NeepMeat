package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseHorFacingBlock;
import com.neep.neepmeat.blockentity.fluid.SpigotBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SpigotBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public SpigotBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return context.getPlayerLookDirection().getAxis().isVertical() ? getDefaultState() :
                this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new SpigotBlockEntity(pos, state);
    }
}
