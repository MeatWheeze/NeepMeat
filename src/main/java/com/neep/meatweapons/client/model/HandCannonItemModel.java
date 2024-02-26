package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.HandCannonItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.GeoModel;

public class HandCannonItemModel extends GeoModel<HandCannonItem>
{
    @Override
    public Identifier getModelResource(HandCannonItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/hand_cannon-thin.geo.json");
    }

    @Override
    public Identifier getTextureResource(HandCannonItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/hand_cannon-thin.png");
    }

    @Override
    public Identifier getAnimationResource(HandCannonItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/hand_cannon.animation.json");
    }
}
