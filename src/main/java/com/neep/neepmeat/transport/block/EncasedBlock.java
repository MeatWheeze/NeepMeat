package com.neep.neepmeat.transport.block;

import com.neep.neepmeat.transport.block.energy_transport.VascularConduitBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EncasedBlock
{
    default boolean replaceUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof BlockItem bi && bi.getBlock() != this
                && canReplace(stack, bi)
                && world.getBlockEntity(pos) instanceof EncasedBlockEntity be)
        {
            BlockState camoState = bi.getBlock().getPlacementState(new ItemPlacementContext(player, hand, stack, hit));
            be.setCamoState(camoState);

            world.updateListeners(pos, state, state, Block.REDRAW_ON_MAIN_THREAD);
            world.playSound(player, pos, SoundEvents.BLOCK_SCAFFOLDING_BREAK, SoundCategory.BLOCKS, 1, 1);

            return true;
        }
        return false;
    }

    boolean canReplace(ItemStack stack, BlockItem blockItem);
}
