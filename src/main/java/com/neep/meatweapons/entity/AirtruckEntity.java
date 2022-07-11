package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
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

    public final float maxSpeed = 0.05f;
    protected final float forwardsAccel = 0.004f;
    public float forwardsVelocity;

    protected boolean accelerating;
    protected boolean braking;
    protected short soundStage = 0;

    public AirtruckEntity(EntityType<? extends AbstractVehicleEntity> type, World world)
    {
        super(type, world);
    }

    public static AirtruckEntity create(World world)
    {
        return new AirtruckEntity(MeatWeapons.AIRTRUCK, world);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    protected void updateMotion()
    {
        if (!this.hasPassengers())
        {
            return;
        }

        float upVelocity = 0.0f;
        if (this.pressingLeft)
            this.yawVelocity -= 1.0f;
        if (this.pressingRight)
            this.yawVelocity += 1.0f;

        this.setYaw(this.getYaw() + this.yawVelocity);

        if (this.pressingForward && !this.pressingBack)
        {
            this.forwardsVelocity = Math.min(this.forwardsVelocity + forwardsAccel, maxSpeed);
            this.accelerating = true;
        }
        else if (!this.pressingForward && this.pressingBack)
        {
            this.forwardsVelocity = Math.max(this.forwardsVelocity - forwardsAccel, -maxSpeed);
        }
        else
        {
            this.forwardsVelocity *= this.velocityDecay;
        }

        if (this.pressingUp)
        {
            upVelocity += 0.08;
        }
        if (this.pressingDown)
        {
            upVelocity -= 0.08;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * forwardsVelocity,
                upVelocity,
                MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * forwardsVelocity));
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
//        MinecraftClient.getInstance().getSoundManager().play(new AirtruckSoundInstance(this));
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

    @Override
    public ItemStack asStack()
    {
        return MeatWeapons.AIRTRUCK_ITEM.getDefaultStack();
    }

    @Override
    public SoundEvent getDamageSound()
    {
        return SoundEvents.ENTITY_IRON_GOLEM_DAMAGE;
    }
}
