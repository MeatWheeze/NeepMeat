package com.neep.neepmeat.machine.item_mincer;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.grinder.GrinderBlockEntity;
import com.neep.neepmeat.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemMincerBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public ItemMincerBlock(String itemName, int loreLines, Settings settings)
    {
        super(itemName, 64, false, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof ItemMincerBlockEntity be)
        {
            return ActionResult.success(ItemUtils.singleVariantInteract(player, hand, be.storage.inputStorage));
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockEntity(pos) instanceof ItemMincerBlockEntity be)
        {
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, be.storage.inputStorage.getAsStack());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        super.onLandedUpon(world, state, pos, entity, fallDistance);
        if (world.getBlockEntity(pos) instanceof ItemMincerBlockEntity be && !world.isClient() && entity instanceof ItemEntity item)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ItemVariant variant = ItemVariant.of(item.getStack());
                long inserted = be.storage.inputStorage.insert(variant, item.getStack().getCount(), transaction);
                item.getStack().decrement((int) inserted);
                transaction.commit();
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ITEM_MINCER.instantiate(pos, state);
    }
}
