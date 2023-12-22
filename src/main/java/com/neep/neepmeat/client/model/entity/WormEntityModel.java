package com.neep.neepmeat.client.model.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.worm.WormEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WormEntityModel extends AnimatedGeoModel<WormEntity>
{
    @Override
    public Identifier getModelLocation(WormEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "geo/god_worm.geo.json");
    }

    @Override
    public Identifier getTextureLocation(WormEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "textures/block/duat_stone.png");
    }

    @Override
    public Identifier getAnimationFileLocation(WormEntity animatable)
    {
        return new Identifier(NeepMeat.NAMESPACE, "animations/god_worm.animation.json");
    }
}
