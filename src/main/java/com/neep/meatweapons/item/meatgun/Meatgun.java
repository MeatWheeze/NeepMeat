package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.MeatgunModule;

import java.util.function.Supplier;

public interface Meatgun
{
    MeatgunModule createBase(MeatgunComponent.Listener listener);

    Supplier<Object> createAnimationManager(MeatgunComponent component);
}
