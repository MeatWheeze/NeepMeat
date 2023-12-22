package com.neep.meatweapons.client.model;

import com.neep.megastructure.Ref;
import com.neep.megastructure.item.FusionCannonItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FusionCannonItemModel extends AnimatedGeoModel<FusionCannonItem>
{
    @Override
    public Identifier getModelLocation(FusionCannonItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(Ref.NAMESPACE, "geo/fusion.geo.json");
    }

    @Override
    public Identifier getTextureLocation(FusionCannonItem object)
    {
        return new Identifier(Ref.NAMESPACE, "textures/general/fusion.png");
    }

    @Override
    public Identifier getAnimationFileLocation(FusionCannonItem animatable)
    {
        return new Identifier(Ref.NAMESPACE, "animations/fusion.animation.json");
    }
}
