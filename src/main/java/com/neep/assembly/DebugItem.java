package com.neep.assembly;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DebugItem extends Item
{
    public DebugItem(Settings settings)
    {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isOf(Assembly.PLATFORM))
        {
            if (!world.isClient)
            {
                AssemblyUtils.assembleBlocks(world, pos);
            }
            return ActionResult.SUCCESS;
        }
        if (world.isClient)
        {
//            context.getPlayer().setVelocity(new Vec3d(0, 1, 0));
            context.getPlayer().travel(new Vec3d(50, 0, 50));
        }
        return ActionResult.FAIL;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
    {
        System.out.println(entity);
        return ActionResult.SUCCESS;
    }
}
