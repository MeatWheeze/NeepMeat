package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionType;

public interface AmmunitionStoringModule
{
    int capacity();

    AmmunitionType ammoType();

    int amount();

    int insert(int maxAmount);

    int extract(int maxAmount);
}
