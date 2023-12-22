package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.HeavyCannonItem;
import com.neep.meatweapons.item.LMGItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HeavyCannonItemModel extends AnimatedGeoModel<HeavyCannonItem>
{
    @Override
    public Identifier getModelResource(HeavyCannonItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/heavy_cannon.geo.json");
    }

    @Override
    public Identifier getTextureResource(HeavyCannonItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/heavy_cannon.png");
    }

    @Override
    public Identifier getAnimationResource(HeavyCannonItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/heavy_cannon.animation.json");
    }
}
