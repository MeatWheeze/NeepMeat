package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.LMGItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class LMGItemModel extends GeoModel<LMGItem>
{
    @Override
    public Identifier getModelResource(LMGItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/light_machine_gun.geo.json");
    }

    @Override
    public Identifier getTextureResource(LMGItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/light_machine_gun.png");
    }

    @Override
    public Identifier getAnimationResource(LMGItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/light_machine_gun.animation.json");
    }
}
