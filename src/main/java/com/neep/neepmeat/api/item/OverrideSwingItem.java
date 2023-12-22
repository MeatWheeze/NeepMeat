package com.neep.neepmeat.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface OverrideSwingItem
{
    default boolean onSwing(ItemStack stack, PlayerEntity player)
    {
        return true;
    }
}
