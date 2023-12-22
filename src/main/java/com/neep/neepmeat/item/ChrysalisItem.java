package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.entity.bovine_horror.BovineHorrorEntity;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.implant.player.EntityImplantInstaller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public class ChrysalisItem extends BaseItem implements EntityImplantInstaller
{
    public ChrysalisItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings.rarity(Rarity.RARE));
    }

    @Override
    public void install(Entity entity)
    {
        if (entity instanceof CowEntity cow)
        {
            cow.discard();
            World world = entity.getEntityWorld();
            if (!world.isClient())
            {
                BovineHorrorEntity bovineHorror = NMEntities.BOVINE_HORROR.create(world);
                bovineHorror.setPos(cow.getX(), cow.getY() + 1, cow.getZ());
                world.spawnEntity(bovineHorror);
            }
        }
    }

}
