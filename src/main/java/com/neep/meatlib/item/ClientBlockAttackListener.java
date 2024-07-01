package com.neep.meatlib.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ClientBlockAttackListener
{
    void onAttackBlock(ItemStack stack, PlayerEntity player);

    void onFinishAttackBlock(ItemStack stack, PlayerEntity player);
}
