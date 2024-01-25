package com.neep.neepmeat.transport.block.energy_transport;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.transport.api.pipe.VascularConduit;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
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
        if (player.getStackInHand(hand).isOf(NMBlocks.VASCULAR_CONDUIT.asItem()))
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

//    @Override
//    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
//    {
//        super.onBlockAdded(state, world, pos, oldState, notify);
//        updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.ADDED);
//    }
//
//    @Override
//    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
//    {
//        if (VascularConduit.find(world, sourcePos, world.getBlockState(sourcePos)) == null && !sourceBlock.equals(this))
//        {
//            updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.CHANGED);
//        }
//    }
}
