package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.player.implant.ImplantInstaller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;

public class ChrysalisItem extends BaseItem implements ImplantInstaller
{
    public ChrysalisItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
    }

    @Override
    public void install(Entity entity)
    {
        if (entity instanceof CowEntity cow)
        {
            cow.kill();
        }
    }
}
