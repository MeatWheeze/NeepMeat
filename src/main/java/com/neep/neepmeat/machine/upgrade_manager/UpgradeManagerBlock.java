package com.neep.neepmeat.machine.upgrade_manager;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpgradeManagerBlock extends BaseHorFacingBlock
{
    public UpgradeManagerBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return super.getPlacementState(context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof UpgradeManagerBlockEntity be)
        {
            player.openHandledScreen(be);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
