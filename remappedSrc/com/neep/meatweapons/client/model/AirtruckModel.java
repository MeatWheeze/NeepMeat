package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.AirtruckEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AirtruckModel extends AnimatedGeoModel<AirtruckEntity>
{
    @Override
    public Identifier getModelResource(AirtruckEntity object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/airtruck.geo.json");
    }

    @Override
    public Identifier getTextureResource(AirtruckEntity object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/entity/airtruck.png");
    }

    @Override
    public Identifier getAnimationResource(AirtruckEntity animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/airtruck.animation.json");
    }
}
