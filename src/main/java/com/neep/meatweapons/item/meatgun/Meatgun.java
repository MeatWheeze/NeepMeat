package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.MeatgunModule;

import java.util.function.Supplier;

public interface Meatgun
{
    MeatgunModule createBase(MeatgunComponent.Listener listener);

    /**
     * @return An anonymous instance of {@link Supplier<MeatgunAnimationManager>} so that it won't get loaded on the server.
     */
    Supplier<Object> createAnimationManager();
}
