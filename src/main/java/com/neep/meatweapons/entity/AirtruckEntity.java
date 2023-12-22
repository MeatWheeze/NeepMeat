package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.AirtruckItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AirtruckEntity extends AbstractVehicleEntity implements IAnimatable
{
    private final AnimationFactory factory = new AnimationFactory(this);

    public AirtruckEntity(EntityType<? extends AbstractVehicleEntity> type, World world)
    {
        super(type, world);
    }

    public static AirtruckEntity create(World world)
    {
        return new AirtruckEntity(MeatWeapons.AIRTRUCK, world);
    }

    @Override
    public void registerControllers(AnimationData data)
    {
        data.addAnimationController(new AnimationController<AirtruckEntity>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
    {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airtruck.fly", true));
        return PlayState.CONTINUE;
    }
}
