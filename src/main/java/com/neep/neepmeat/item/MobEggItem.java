package com.neep.neepmeat.item;

import com.neep.meatlib.item.IMeatItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class MobEggItem extends Item implements IMeatItem
{
    private final String registryName;

    public MobEggItem(Settings settings, String registryName)
    {
        super(settings);
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return null;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack)
    {
        return true;
    }
}
