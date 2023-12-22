package com.neep.neepmeat.item;

import com.neep.neepmeat.init.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BaseBucketItem extends BucketItem implements NMItem
{
private final String registryName;

    public BaseBucketItem(String registryName, Fluid fluid, Settings settings)
    {
        super(fluid, settings);
        this.registryName=registryName;
        ItemInit.putItem(registryName, this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        return super.use(world, user, hand);
    }
}
