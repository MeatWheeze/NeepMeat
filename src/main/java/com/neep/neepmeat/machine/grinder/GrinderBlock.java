package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.machine.content_detector.ContentDetectorBlock;
import com.neep.neepmeat.recipe.GrindingRecipe;
import com.neep.neepmeat.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class GrinderBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public GrinderBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque().solidBlock(ContentDetectorBlock::never));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be)
        {
            return ActionResult.success(ItemUtils.singleVariantInteract(player, hand, be.getStorage().getInputStorage()));
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new GrinderBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()) && !world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be)
            {
                be.storage.dropItems(world, pos);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity)
    {
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be && !world.isClient())
        {
//            be.update((ServerWorld) world, pos, pos, state);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be && !world.isClient())
        {
//            be.update((ServerWorld) world, pos, fromPos, state);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
//        GrindingRecipe recipe;
//        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be && (recipe = be.getCurrentRecipe()) != null)
//        {
////            Item output = recipe.getItemOutput().resource();
//            ItemStack stack1 = be.getStorage().getInputStorage().getResource().toStack(1);
//            ItemStack stack2 = recipe.getItemOutput().resource().getDefaultStack();
//
//            for (int it = 0; it < 5; ++it)
//            {
//                double d = (double) pos.getX() + 0.5;
//                double e = pos.getY() + 0.7;
//                double f = (double) pos.getZ() + 0.5;
//
//                double h = (random.nextDouble() - 0.5) * 0.1;
//                double i = (random.nextDouble() - 0.5) * 0.1 + 0.2;
//                double j = (random.nextDouble() - 0.5) * 0.1;
//
//                world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack1.isEmpty() ? stack2 : stack1), d, e, f, h, i, j);
//            }
//        }
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        super.onLandedUpon(world, state, pos, entity, fallDistance);
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be && !world.isClient() && entity instanceof ItemEntity item)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ItemVariant variant = ItemVariant.of(item.getStack());
                long inserted = be.getStorage().getInputStorage().insert(variant, item.getStack().getCount(), transaction);
                item.getStack().decrement((int) inserted);
                transaction.commit();
            }
        }
    }
}
