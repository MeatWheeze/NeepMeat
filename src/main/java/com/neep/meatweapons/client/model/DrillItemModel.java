package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.AssaultDrillItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DrillItemModel extends GeoModel<AssaultDrillItem>
{
    @Override
    public Identifier getModelResource(AssaultDrillItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "geo/assault_drill.geo.json");
    }

    @Override
    public Identifier getTextureResource(AssaultDrillItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/assault_drill.png");
    }

    @Override
    public Identifier getAnimationResource(AssaultDrillItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/assault_drill.animation.json");
    }
}
