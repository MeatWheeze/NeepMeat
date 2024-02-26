package com.neep.neepmeat.transport.block.energy_transport;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.energy_transport.entity.VSCBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VSCBlock extends BaseFacingBlock implements BlockEntityProvider
{
    // Vascular Source Converter!
    public VSCBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (VascularConduitBlock.matches(player.getStackInHand(hand)))
            return super.onUse(state, world, pos, player, hand, hit);

        if (world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            if (!world.isClient())
                player.openHandledScreen(be);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.VSC.instantiate(pos, state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            be.updateThing();
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            be.updateThing();
        }
    }
}
