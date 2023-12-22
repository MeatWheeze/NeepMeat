package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Environment(value = EnvType.CLIENT)
public class IntegratorEggModel<I extends BlockEntity> extends AnimatedGeoModel<IntegratorBlockEntity>
{
    @Override
    public Identifier getModelResource(IntegratorBlockEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "geo/integrator.geo.json");
    }

    @Override
    public Identifier getTextureResource(IntegratorBlockEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "textures/entity/integrator_basic.png");
    }

    @Override
    public Identifier getAnimationResource(IntegratorBlockEntity animatable)
    {
        return new Identifier(NeepMeat.NAMESPACE, "animations/integrator.animation.json");
    }
}
