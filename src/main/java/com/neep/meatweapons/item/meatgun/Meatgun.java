package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.meatgun.module.MeatgunModule;

public interface Meatgun
{
    MeatgunModule createBase(MeatgunComponent.Listener listener);
}
