package com.neep.neepmeat.block.item_transport;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.blockentity.ItemBufferBlockEntity;
import com.neep.neepmeat.storage.WritableStackStorage;
import com.neep.neepmeat.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ItemBufferBlock extends BaseBlock implements BlockEntityProvider
{
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public ItemBufferBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        return DEFAULT_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new ItemBufferBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof ItemBufferBlockEntity be && !world.isClient)
        {
            ItemStack stack = player.getStackInHand(hand);
            WritableStackStorage storage = be.getStorage(null);
            if ((storage.isEmpty() || storage.getResource().matches(stack)) && !stack.isEmpty())
            {
                Transaction transaction = Transaction.openOuter();

                long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);

                transaction.commit();
            }
            else if ((stack.isEmpty() || !storage.getResource().matches(stack)) && !storage.isEmpty())
            {
                Transaction transaction = Transaction.openOuter();

                player.giveItemStack(storage.getAsStack());
                storage.extract(storage.getResource(), storage.getAmount(), transaction);

                transaction.commit();
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemBufferBlockEntity be && !world.isClient())
        {
            onEntityCollided(world, pos, state, entity, be);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof ItemBufferBlockEntity be)
            {
                ItemUtils.scatterItems(world, pos, be.getStorage(null));
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        ItemBufferBlockEntity be = (ItemBufferBlockEntity) world.getBlockEntity(pos);
        int maxCount = be.getStorage(null).getResource().toStack().getMaxCount();
        return maxCount > 0 ? (int) Math.ceil((float) be.getStorage(null).getAmount() / (float) maxCount * 16) : 0;
    }

    // Add dropped items to inventory
    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, ItemBufferBlockEntity be)
    {
        if (!world.isClient && entity instanceof ItemEntity item)
        {
            be.extractFromItem(item);
        }
    }
}
