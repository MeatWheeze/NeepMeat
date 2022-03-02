package com.neep.neepmeat.item;

import com.neep.neepmeat.fluid.RealisticFluid;
import com.neep.neepmeat.init.FluidInitialiser;
import com.neep.neepmeat.item.base.BaseSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DaggerItem extends BaseSwordItem
{
    public DaggerItem(String registryName, Settings settings)
    {
        super(registryName, settings.maxDamage(128));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        super.postHit(stack, target, attacker);

        World world = target.getEntityWorld();
        if (target.isDead() && !world.isClient)
        {
            BlockPos pos = target.getBlockPos();
//                world.setBlockState(target.getBlockPos(), BlockInitialiser.STILL_BLOOD.getFlowing(1, false).getBlockState(), Block.NOTIFY_ALL);
            RealisticFluid.incrementLevel(world, pos, world.getBlockState(pos), FluidInitialiser.FLOWING_BLOOD);
        }
        return true;
    }
}
