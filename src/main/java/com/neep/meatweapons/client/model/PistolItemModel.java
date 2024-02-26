package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.MachinePistolItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.GeoModel;

public class PistolItemModel extends GeoModel<MachinePistolItem>
{
    @Override
    public Identifier getModelResource(MachinePistolItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/machine_pistol.geo.json");
    }

    @Override
    public Identifier getTextureResource(MachinePistolItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/machine_pistol.png");
    }

    @Override
    public Identifier getAnimationResource(MachinePistolItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/machine_pistol.animation.json");
    }
}
