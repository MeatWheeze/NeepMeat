package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.meatgun.module.BasePistolModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;

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
}
