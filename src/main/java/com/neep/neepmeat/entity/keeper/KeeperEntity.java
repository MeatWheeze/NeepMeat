package com.neep.neepmeat.entity.keeper;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.FusionCannonItem;
import com.neep.neepmeat.init.NMItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

public class KeeperEntity extends HostileEntity implements RangedAttackMob
{
    private final ServerBossBar bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);

    protected final KeeperRangedAttackGoal<KeeperEntity> rangedAttackGoal = new KeeperRangedAttackGoal<>(this, 1.0, 20, 15.0f, MWItems.FUSION_CANNON);
    protected final KeeperMeleeGoal meleeAttackGoal = new KeeperMeleeGoal(this, 2.2, false){

    };
    protected ItemStack equipped = new ItemStack(MWItems.FUSION_CANNON);

//    protected GoalSelector targetSelector = new GoalSelector(world.getProfilerSupplier());
//    protected GoalSelector goalSelector = new GoalSelector(world.getProfilerSupplier());

    public KeeperEntity(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
        updateAttackType();
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    public float getMovementSpeed()
    {
        return 0.2f;
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
    {
        updateAttackType();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initGoals()
    {
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.add(1, new SwitchWeaponGoal(this, 7));

        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        goalSelector.add(4, new KeeperCritGoal(this, 8f));
        goalSelector.add(3, new KeeperDodgeGoal(this, 8f));
        goalSelector.add(3, new KeeperRetreatGoal(this, 8f, 16));
        goalSelector.add(4, new KeeperHealGoal(this, 8f, 16));
    }

    public boolean shouldHeal()
    {
        return getHealth() < 0.75 * getMaxHealth();
    }

    public void updateAttackType()
    {
        if (this.getWorld() == null || this.getWorld().isClient) return;

        this.goalSelector.remove(this.meleeAttackGoal);
        this.goalSelector.remove(this.rangedAttackGoal);

        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, MWItems.FUSION_CANNON));
        if (itemStack.isOf(MWItems.FUSION_CANNON))
        {
            int i = 20;
            this.rangedAttackGoal.setAttackInterval(i);
            this.goalSelector.add(4, this.rangedAttackGoal);
        }
        else
        {
            this.goalSelector.add(4, this.meleeAttackGoal);
        }
    }


    @Override
    protected void mobTick()
    {
        super.mobTick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    boolean isPlayerStaring(LivingEntity entity)
    {
        HitResult result = entity.raycast(10, 0.5f, false);
        Optional<Vec3d> entityHit = getBoundingBox().raycast(entity.getCameraPosVec(0.5f), result.getPos());

        return entityHit.isPresent();

//        Vec3d vec3d = entity.getRotationVec(1.0f).normalize();
//        Vec3d vec3d2 = new Vec3d(this.getX() - entity.getX(), this.getEyeY() - entity.getEyeY(), this.getZ() - entity.getZ());
//        double distance = vec3d2.length();
//        double e = vec3d.dotProduct(vec3d2.normalize());
//        if (e > 1.0 - 0.025 / distance)
//        {
//            return entity.canSee(this);
//        }
//        return false;
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
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        updateAttackType();
    }

    @Override
    public float getHealth()
    {
        return super.getHealth();
    }

    @Override
    public Iterable<ItemStack> getArmorItems()
    {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.MAINHAND ? equipped : ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {
        if (slot == EquipmentSlot.MAINHAND)
            equipped = stack;
        if (!getWorld().isClient()) updateAttackType();
    }

    @Override
    public Arm getMainArm()
    {
        return Arm.LEFT;
    }

    @Override
    public void attack(LivingEntity target, float pullProgress)
    {
        ItemStack itemStack = getStackInHand(Hand.MAIN_HAND);
        if (!getWorld().isClient() && itemStack.isOf(MWItems.FUSION_CANNON))
        {
            ((FusionCannonItem) itemStack.getItem()).fireBeam(getWorld(), this, target, itemStack);
        }
    }

    protected void switchWeapon(WeaponType type)
    {
        switch (type)
        {
            case NONE -> equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            case MELEE -> equipStack(EquipmentSlot.MAINHAND, new ItemStack(NMItems.SLASHER));
            case RANGED -> equipStack(EquipmentSlot.MAINHAND, new ItemStack(MWItems.FUSION_CANNON));
        }
    }

    protected enum WeaponType
    {
        NONE, MELEE, RANGED;
    }

    public static class SwitchWeaponGoal extends Goal
    {
        protected final KeeperEntity entity;
        protected float meleeRange;

        public SwitchWeaponGoal(KeeperEntity entity, float meleeRange)
        {
            this.entity = entity;
            this.meleeRange = meleeRange;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        @Override
        public boolean canStart()
        {
            boolean hasTarget = entity.getTarget() != null;
            boolean holdingRanged = entity.isHolding(s -> s.getItem() instanceof BaseGunItem);
            boolean inMeleeRange = hasTarget && entity.isInRange(entity.getTarget(), meleeRange);

            return hasTarget && holdingRanged == inMeleeRange;
        }

        @Override
        public void tick()
        {
            boolean hasTarget = entity.getTarget() != null;
            boolean holdingRanged = entity.isHolding(s -> s.getItem() instanceof BaseGunItem);
            boolean inMeleeRange = hasTarget && entity.isInRange(entity.getTarget(), meleeRange);

            if (!hasTarget)
            {
                entity.switchWeapon(WeaponType.NONE);
            }
            else if (inMeleeRange && holdingRanged)
            {
                entity.switchWeapon(WeaponType.MELEE);
            }
            else if (!inMeleeRange && !holdingRanged)
            {
                entity.switchWeapon(WeaponType.RANGED);
            }
        }

        @Override
        public boolean shouldContinue()
        {
            return false;
        }
    }

    public static class KeeperMeleeGoal extends MeleeAttackGoal
    {
        public KeeperMeleeGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle)
        {
            super(mob, speed, pauseWhenMobIdle);
        }

        @Override
        public double getSquaredMaxAttackDistance(LivingEntity entity)
        {
            return super.getSquaredMaxAttackDistance(entity);
        }

        //        public double getAttackRange()
//        {
//            return Math.sqrt(getSquaredMaxAttackDistance(mob.getTarget()));
//        }
    }
}
