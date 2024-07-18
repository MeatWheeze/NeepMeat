package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionProvider;
import com.neep.meatweapons.meatgun.AmmunitionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public interface AmmunitionRequiringModule extends MeatgunModule
{
    AmmunitionType ammoType();

    // Not really necessary anymore
    default int capacity() { return Integer.MAX_VALUE; }

    boolean consume(int amount, Inventory inventory, PlayerEntity player);

    boolean reloadFrom(AmmunitionProvider provider, PlayerEntity player);
}
