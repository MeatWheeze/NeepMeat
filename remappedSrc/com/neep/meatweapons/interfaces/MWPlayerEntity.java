package com.neep.meatweapons.interfaces;

import com.neep.meatweapons.entity.PlayerWeaponManager;

public interface MWPlayerEntity
{
    default PlayerWeaponManager meatweapons$getWeaponManager() {return null;}
}