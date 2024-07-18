package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public interface AmmunitionRequiringModule extends MeatgunModule
{
    AmmunitionType ammoType();

    boolean consume(int amount, Inventory inventory, PlayerEntity player);
}
