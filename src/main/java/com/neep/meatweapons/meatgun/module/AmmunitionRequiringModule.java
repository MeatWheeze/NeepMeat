package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionProvider;
import com.neep.meatweapons.meatgun.AmmunitionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public interface AmmunitionRequiringModule extends MeatgunModule
{
    AmmunitionType ammoType();

    int capacity();

    boolean consume(int amount, Inventory inventory, PlayerEntity player);

    boolean reloadFrom(AmmunitionProvider provider, PlayerEntity player);
}
