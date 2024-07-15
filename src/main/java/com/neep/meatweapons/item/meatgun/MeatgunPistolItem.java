package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.client.meatgun.animation.MeatgunAnimation;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.BasePistolModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;

import java.util.function.Supplier;

public class MeatgunPistolItem extends MeatgunItem
{
    public MeatgunPistolItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
    }

    @Override
    public MeatgunModule createBase(MeatgunComponent.Listener listener)
    {
        return new BasePistolModule(listener);
    }

    @Override
    public Supplier<Object> createAnimationManager(MeatgunComponent component)
    {
        return new Supplier<Object>()
        {
            @Override
            public Object get()
            {
                return new MeatgunAnimationManager(MeatgunAnimation.EMPTY);
            }
        };
    }
}
