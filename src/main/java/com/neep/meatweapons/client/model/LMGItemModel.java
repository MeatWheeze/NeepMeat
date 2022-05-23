package com.neep.meatweapons.client.model;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.LMGItem;
import com.neep.meatweapons.item.MachinePistolItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LMGItemModel extends AnimatedGeoModel<LMGItem>
{
    @Override
    public Identifier getModelLocation(LMGItem object)
    {
        // Apparently, the model folder MUST be called 'geo'.
        return new Identifier(MeatWeapons.NAMESPACE, "geo/light_machine_gun.geo.json");
    }

    @Override
    public Identifier getTextureLocation(LMGItem object)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/general/light_machine_gun.png");
    }

    @Override
    public Identifier getAnimationFileLocation(LMGItem animatable)
    {
        return new Identifier(MeatWeapons.NAMESPACE, "animations/light_machine_gun.animation.json");
    }
}
