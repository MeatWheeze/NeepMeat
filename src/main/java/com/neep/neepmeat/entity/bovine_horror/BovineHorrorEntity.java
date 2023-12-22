package com.neep.neepmeat.entity.bovine_horror;

import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.util.SightUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
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
    protected static final TrackedData<Integer> SYNC_ID = DataTracker.registerData(BovineHorrorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Float> VISIBILITY = DataTracker.registerData(BovineHorrorEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.horror.wave", ILoopType.EDefaultLoopTypes.LOOP);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final String moveIdleController = "move_idle";
    private final String attackController = "attack";

    public float prevVisibility = 0;

    protected final BovineHorrorMeleeAttackGoal meleeAttackGoal = new BovineHorrorMeleeAttackGoal(this);
    protected boolean phase2 = false; // State pattern? Pah!

    private final ServerBossBar bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);

    public BovineHorrorEntity(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
        initCustomGoals();
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        dataTracker.startTracking(SYNC_ID, 0);
        dataTracker.startTracking(VISIBILITY, 0.0f);
    }

    public int getSyncId()
    {
        return dataTracker.get(SYNC_ID);
    }

    @Override
    protected void initGoals()
    {
        super.initGoals();
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));

//        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
//        goalSelector.add(3, new FaceTowardTargetGoal(this));
//        goalSelector.add(2, new BovineHorrorMeleeAttackGoal(this, 1.0, false));
    }

    protected void initCustomGoals()
    {
        goalSelector.add(2, meleeAttackGoal);
        goalSelector.add(2, new HorrorMoveToTargetGoal(this, 1.2, true));
        goalSelector.add(2, new HorrorAcidAttackGoal(this, 3, 2));
    }

    @Override
    public void registerControllers(AnimationData animationData)
    {
        animationData.addAnimationController(new AnimationController<>(this, moveIdleController, 5, this::moveController));
        animationData.addAnimationController(new AnimationController<>(this, attackController, 5, this::attackControl));
    }

    @Override
    public boolean tryAttack(Entity target)
    {
        setEvent(MobEvent.ATTACK);
        return super.tryAttack(target);
    }

    private <T extends IAnimatable> PlayState attackControl(final AnimationEvent<T> event)
    {
        if (handSwinging && event.getController().getAnimationState().equals(AnimationState.Stopped))
        {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.horror.melee", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            handSwinging = false;
        }

        return PlayState.CONTINUE;
    }

    protected <E extends BovineHorrorEntity> PlayState moveController(final AnimationEvent<E> event)
    {
        event.getController().setAnimation(IDLE_ANIM);

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
        Vec3d origin = getPos().add(0, 1.5, 0);
        for (int i = 0; i < 150; ++i)
        {
            spawnParticle(world, NMParticles.BODY_COMPOUND_SHOWER, random, origin);
        }
    }

    public void spawnParticle(World world, ParticleEffect effect, Random random, Vec3d origin)
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
        float visibility = world.isClient() ? prevVisibility : getVisibility();
        return super.isInvisibleTo(player) || (!SightUtil.canPlayerSee(player, this) && visibility == 0);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (getHealth() < getMaxHealth() / 2 && !phase2)
        {
            phase2 = true;
            this.bossBar.setDarkenSky(true);

            goalSelector.clear();
            goalSelector.add(1, new BHPhaseActionGoal(this));
            navigation.stop();
        }

        if (!world.isClient())
        {
            prevVisibility = getVisibility();
            if (world.getTime() % 60 == 0)
            {
                float p = random.nextFloat();
                if (p > 0.5 && getVisibility() == 0)
                {
                    setVisibility(0.9f);
                }
            }

            if (isAlive())
            {
                setVisibility(Math.max(0, getVisibility() - 0.1f));
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
        setVisibility(1);


        if (world.isClient())
        {
            for (int i = 0; i < 100; ++i)
            {
                world.addParticle(NMParticles.BODY_COMPOUND_SHOWER,
                        getX() + (random.nextFloat() - 0.5) * 3,
                        getY() + (random.nextFloat()) * 2 + 0.5,
                        getZ() + (random.nextFloat() - 0.5) * 3,
                        0, 0.1, 0
                );
            }
        }
    }

    @Override
    protected void mobTick()
    {
        this.bossBar.setPercent(getHealth() / getMaxHealth());
        super.mobTick();

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("phase2", phase2);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.phase2 = nbt.getBoolean("phase2");
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
//        return MathHelper.lerp(tickDelta, prevVisibility, getVisibility());
        return getVisibility();
    }

//    @Override
//    public void onAnimationSync(int id, int state)
//    {
//        if (state == MELEE_ANIM_ID)
//        {
//            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, moveIdleController);
//            if (controller.getAnimationState() == AnimationState.Stopped)
//            {
//                controller.markNeedsReload();
//                controller.setAnimation(new AnimationBuilder().addAnimation("animation.horror.melee", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
//            }
//        }
//    }

    public void setEvent(MobEvent event)
    {
        dataTracker.set(SYNC_ID, event.ordinal());
    }

    public MobEvent getEvent()
    {
        return MobEvent.values()[dataTracker.get(SYNC_ID)];
    }

    public void consumeEvent()
    {
        dataTracker.set(SYNC_ID, 0);
    }

    public float getVisibility()
    {
        return dataTracker.get(VISIBILITY);
    }

    public void setVisibility(float visibility)
    {
        dataTracker.set(VISIBILITY, visibility);
    }

    public float getAttackRange(Entity entity)
    {
        return 4;
    }

    public boolean canMelee(Entity entity)
    {
        return distanceTo(entity) <= getAttackRange(entity);
    }

    public boolean isInRange(Entity entity)
    {
        return distanceTo(entity) <= getAttackRange(entity) * 0.5;
    }

    public boolean isInMoveRange(Entity entity)
    {
        return distanceTo(entity) <= 20;
    }

    enum MobEvent
    {
        NONE,
        ATTACK
    }
}