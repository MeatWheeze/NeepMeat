package com.neep.meatweapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

    public AirtruckEntity(EntityType<? extends Entity> type, World world)
    {
        super(type, world);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand)
    {
        if (!this.world.isClient)
        {
            player.setYaw(this.getYaw());
            player.setPitch(this.getPitch());
            player.startRiding(this);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return BoatEntity.canCollide(this, other);
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
