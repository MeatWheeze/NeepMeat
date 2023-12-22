package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseBlock;
import com.neep.neepmeat.blockentity.ItemBufferBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemBufferBlock extends BaseBlock implements BlockEntityProvider
{
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);

    public ItemBufferBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
//        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
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
//        System.out.println(world.getBlockEntity(pos));
        if (world.getBlockEntity(pos) instanceof ItemBufferBlockEntity be && !world.isClient)
        {
            ItemStack stack = player.getStackInHand(hand);
            if (be.isResourceBlank() || be.getResource().matches(stack))
            {
                Transaction transaction = Transaction.openOuter();

                long inserted = be.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);

                transaction.commit();
            }
            else if (stack.isEmpty() || !be.getResource().matches(stack))
            {
                Transaction transaction = Transaction.openOuter();

                player.giveItemStack(be.getResource().toStack((int) be.getAmount()));
                be.extract(be.getResource(), be.getAmount(), transaction);

                transaction.commit();
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemBufferBlockEntity be)
        {
            onEntityCollided(world, pos, state, entity, be);
        }
    }

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, ItemBufferBlockEntity be)
    {
        if (!world.isClient && entity instanceof ItemEntity item)
        {
            be.extractFromItem(item);
        }
    }
}
