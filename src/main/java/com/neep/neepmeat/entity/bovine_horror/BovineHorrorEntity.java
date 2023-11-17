package com.neep.neepmeat.entity.bovine_horror;

import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.util.SightUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
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

    public float prevVisibility = 0;
    public float visibility = 0;

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
    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
//        ParticleEffect effect = new BlockStateParticleEffect(ParticleTypes.BLOCK, NMFluids.WORK_FLUID.getDefaultState());
        Vec3d origin = getPos().add(0, 1.5, 0);
        for (int i = 0; i < 150; ++i)
        {
            spawnParticle(world, NMParticles.BODY_COMPOUND_SHOWER, random, origin);
        }
    }

    protected void spawnParticle(World world, ParticleEffect effect, Random random, Vec3d origin)
    {
        double r = random.nextFloat() * 1.5 + 0.5;
        double pitch = (random.nextFloat() + 0.5) * 2 * Math.PI / 2;
        double yaw = (random.nextFloat() + 0.5) * 2 * Math.PI;
        double px = origin.x + r * Math.sin(yaw);
        double pz = origin.z + r * Math.cos(yaw);
        double py = origin.y + Math.sin(pitch);

        double vx = px - origin.x * 1;
        double vy = py - origin.y * 1;
        double vz = pz - origin.z * 1;

        world.addParticle(effect, px, py, pz, vx, vy, vz);
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player)
    {
        return super.isInvisibleTo(player) || (!SightUtil.canPlayerSee(player, this) && getVisibility(1) == 0);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (world.isClient())
        {
            prevVisibility = visibility;
            if (world.getTime() % 60 == 0)
            {
                float p = random.nextFloat();
                if (p > 0.5 && visibility == 0)
                {
                    visibility = 0.9f;
                }
            }

            if (isAlive())
            {
                visibility = Math.max(0, visibility - 0.1f);
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
        visibility = 1;
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

    public float getVisibility(float tickDelta)
    {
        return MathHelper.lerp(tickDelta, prevVisibility, visibility);
    }
}
