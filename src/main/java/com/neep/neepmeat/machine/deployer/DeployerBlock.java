package com.neep.neepmeat.machine.deployer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.machine.motor.MotorBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeployerBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public DeployerBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world.getBlockEntity(pos) instanceof DeployerBlockEntity be)
        {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), be.getResource().toStack((int) be.getAmount()));
            world.updateComparators(pos,this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).getItem() instanceof BlockItem blockitem
            && blockitem.getBlock() instanceof MotorBlock)
        {
            return ActionResult.PASS;
        }
        if (world.getBlockEntity(pos) instanceof DeployerBlockEntity be && !world.isClient() && be.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new DeployerBlockEntity(pos, state);
    }

}
