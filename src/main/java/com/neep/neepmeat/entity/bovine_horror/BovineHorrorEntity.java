package com.neep.neepmeat.entity.bovine_horror;

import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class BovineHorrorEntity extends HostileEntity implements Monster, IAnimatable
{
    protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.horror.wave", ILoopType.EDefaultLoopTypes.LOOP);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private final ServerBossBar bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);

    public BovineHorrorEntity(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    public void registerControllers(AnimationData animationData)
    {
        animationData.addAnimationController(new AnimationController<>(this, "move_idle", 5, this::moveController));
    }

    protected <E extends BovineHorrorEntity> PlayState moveController(final AnimationEvent<E> event)
    {
        event.getController().setAnimation(IDLE_ANIM);
//        if (event.isMoving())
//        {
//            event.getController().setAnimation(FLY_ANIM);

//            return PlayState.CONTINUE;
//        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
    }

    @Override
    public void takeKnockback(double strength, double x, double z)
    {
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player)
    {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player)
    {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    protected void mobTick()
    {
        super.mobTick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return NMSounds.BH_HIT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return NMSounds.HOUND_DEATH;
    }
}
