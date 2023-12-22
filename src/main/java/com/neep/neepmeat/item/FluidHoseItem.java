package com.neep.neepmeat.item;

import com.neep.neepmeat.fluid.RealisticFluid;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FluidHoseItem extends BucketItem
{
    private Fluid fluid;

    public FluidHoseItem(Fluid fluid, Settings settings)
    {
        super(fluid, settings);
        this.fluid = fluid;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
//        BlockHitResult blockHitResult = BucketItem.raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
        BlockHitResult blockHitResult = BucketItem.raycast(world, user, RaycastContext.FluidHandling.ANY);
        if (blockHitResult.getType() == HitResult.Type.MISS)
        {
            return TypedActionResult.pass(itemStack);
        }
        if (blockHitResult.getType() == HitResult.Type.BLOCK)
        {
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);

            if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos2, direction, itemStack))
            {
                return TypedActionResult.fail(itemStack);
            }

            int level = blockState.getFluidState().getLevel();
            int damage = itemStack.getDamage();

            // Remove fluid
            if (user.isSneaking())
            {
                if (blockState.getBlock() instanceof FluidDrainable && damage - level >= 0)
                {
                    if (!world.isClient)
                    {
                        // TODO: replace with drain
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                        itemStack.setDamage(damage - level);
                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(itemStack);
                    }
                }
            }
            else // Place fluid
            {
                if (!world.isClient && damage + 8 <= this.getMaxDamage())
                {
                    boolean isSpecialFluid = blockState.getFluidState().getFluid() instanceof RealisticFluid;
                    BlockPos blockPos3 = (blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER)
                            || isSpecialFluid ? blockPos : blockPos2;
                    if (this.placeFluid(user, world, blockPos3, blockHitResult))
                    {
                        this.onEmptied(user, world, itemStack, blockPos3);
                        if (user instanceof ServerPlayerEntity)
                        {
                            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) user, blockPos3, itemStack);
                        }
                        user.incrementStat(Stats.USED.getOrCreateStat(this));
//                        return TypedActionResult.success(BucketItem.getEmptiedStack(itemStack, user), world.isClient());
                        itemStack.setDamage(damage + 8);
                        return TypedActionResult.success(itemStack);
                    }
                    return TypedActionResult.fail(itemStack);
                }
            }

        }
        return TypedActionResult.pass(itemStack);
    }

}
