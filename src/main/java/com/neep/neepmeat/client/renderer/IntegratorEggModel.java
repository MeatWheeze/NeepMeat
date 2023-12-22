package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class IntegratorEggModel<I extends BlockEntity> extends AnimatedGeoModel<IntegratorBlockEntity>
{
    @Override
    public void setLivingAnimations(IntegratorBlockEntity o, Integer integer, AnimationEvent animationEvent)
    {

    }

    @Override
    public Identifier getModelLocation(IntegratorBlockEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "geo/integrator.geo.json");
    }

    @Override
    public Identifier getTextureLocation(IntegratorBlockEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "textures/entity/integrator_basic.png");
    }

    @Override
    public Identifier getAnimationFileLocation(IntegratorBlockEntity animatable)
    {
        return new Identifier(NeepMeat.NAMESPACE, "animations/integrator.animation.json");
    }
}
