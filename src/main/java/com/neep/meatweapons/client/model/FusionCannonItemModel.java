package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.FusionCannonItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FusionCannonItemModel extends AnimatedGeoModel<FusionCannonItem>
{
    @Override
    public Identifier getModelLocation(FusionCannonItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/fusion.geo.json");
    }

    @Override
    public Identifier getTextureLocation(FusionCannonItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/fusion.png");
    }

    @Override
    public Identifier getAnimationFileLocation(FusionCannonItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/fusion.animation.json");
    }
}
