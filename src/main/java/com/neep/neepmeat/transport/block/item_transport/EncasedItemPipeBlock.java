package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.EncasedBlock;
import com.neep.neepmeat.transport.block.item_transport.entity.EncasedItemPipeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EncasedItemPipeBlock extends ItemPipeBlock implements EncasedBlock
{
    public EncasedItemPipeBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    public VoxelShape getPipeOutlineShape(BlockState state, BlockView world, BlockPos pos)
    {
        return super.getOutlineShape(state, world, pos, ShapeContext.absent());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        if (view.getBlockEntity(pos) instanceof EncasedItemPipeBlockEntity be)
        {
            return be.getCamoShape();
        }
        return super.getOutlineShape(state, view, pos, context);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (world.getBlockEntity(pos) instanceof EncasedItemPipeBlockEntity be)
        {
            be.setCachedState(state);
            be.onNeighbourUpdate();
        }
    }

    @Override
    public void onConnectionUpdate(World world, BlockState state, BlockState newState, BlockPos pos, PlayerEntity entity)
    {
        super.onConnectionUpdate(world, state, newState, pos, entity);
        if (world.getBlockEntity(pos) instanceof EncasedItemPipeBlockEntity be)
        {
            be.onNeighbourUpdate();
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (replaceUse(state, world, pos, player, hand, hit))
        {
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ENCASED_PNEUMATIC_PIPE.instantiate(pos, state);
    }

    @Override
    public boolean canReplace(ItemStack stack, BlockItem blockItem)
    {
        return !(blockItem.getBlock() instanceof EncasedBlock) && !ItemPipeBlock.matches(stack);
    }
}
