package com.neep.neepmeat.entity.follower;

import com.neep.neepmeat.entity.LimbEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class FollowerEntity extends PathAwareEntity
{
    private final List<LimbEntity> limbs = new ArrayList<>();

    @Nullable
    private UUID targetUUID;
    private int maxLimbs = 9;
    private int limbCooldown = 10;

    public FollowerEntity(EntityType<? extends FollowerEntity> type, World world)
    {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createFollowerAttributes()
    {
        return createMobAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void initGoals()
    {
        targetSelector.add(1, new FollowerFindTargetGoal<>(this, p -> true));
        targetSelector.add(0, new FollowerActiveTargetGoal(this, PlayerEntity.class));
        goalSelector.add(0, new FollowerFollowGoal(this, 0.5, 1));
    }

    @Override
    public int getSafeFallDistance()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean damage(DamageSource source, float amount)
    {
        if (source.isIn(DamageTypeTags.IS_FALL))
            return false;

        if (!source.isSourceCreativePlayer())
        {
            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public void tick()
    {
        super.tick();

        var it = limbs.iterator();
        while (it.hasNext())
        {
            LimbEntity limb = it.next();
            if (limb.isRemoved())
                it.remove();
        }

        int limbsToAdd = maxLimbs - limbs.size();

        limbCooldown = Math.max(0, limbCooldown - 1);

        if (limbsToAdd > 0 && limbCooldown == 0 && !getWorld().isClient())
        {
            LimbEntity limb = new LimbEntity(getWorld(), 50 + random.nextInt(20), false);

            double radius = 2;
            double x = getX() + (random.nextFloat() - 1) * radius;
            double z = getZ() + (random.nextFloat() - 1) * radius;

//            BlockPos collisionPos = BlockPos.ofFloored(x, getBlockY(), z);

            limb.setYaw(random.nextInt(360));
            limb.setPos(x, getY() + 0.1, z);
            limbs.add(limb);
            getWorld().spawnEntity(limb);
            limbCooldown = 5 + random.nextInt(20);
        }

        if (getTarget() != null)
        {
            double targetDist = distanceTo(getTarget());
            if (targetDist < 1.4)
            {
                teleportRandomly(getPos());
            }
            else if (targetDist > 30)
            {
                teleportRandomly(getTarget().getPos());
            }
        }
    }

    private void teleportRandomly(Vec3d around)
    {
        if (!this.getWorld().isClient() && this.isAlive())
        {
            double d = around.getX() + (this.random.nextDouble() - 0.5) * 64.0;
            double e = around.getY() + (double) (this.random.nextInt(64) - 32);
            double f = around.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
            teleportTo(d, e, f);
        }
    }

    private boolean teleportTo(double x, double y, double z)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while (mutable.getY() > this.getWorld().getBottomY() && !this.getWorld().getBlockState(mutable).blocksMovement())
        {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.getWorld().getBlockState(mutable);
        boolean bl = blockState.blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2)
        {
            return this.teleport(x, y, z, false);
        }
        else
        {
            return false;
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        if (targetUUID != null)
            nbt.putUuid("persistent_target", targetUUID);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        if (nbt.contains("persistent_target"))
            this.targetUUID = nbt.getUuid("persistent_target");
        else
            this.targetUUID = null;
    }

    public void setPersistentTarget(PlayerEntity player)
    {
        targetUUID = player.getUuid();
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return false;
    }

    @Override
    protected void pushAway(Entity entity)
    {
    }

    @Override
    public void pushAwayFrom(Entity entity)
    {
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }

    @Override
    public boolean cannotDespawn()
    {
        return true;
    }

    private static class FollowerFindTargetGoal<T extends PlayerEntity> extends Goal
    {
        private final FollowerEntity mob;
        private final TargetPredicate predicate;
        protected PlayerEntity targetEntity;

        public FollowerFindTargetGoal(FollowerEntity mob, Predicate<LivingEntity> predicate)
        {
            this.mob = mob;
            this.predicate = TargetPredicate.createAttackable().setPredicate(predicate);
        }

        protected void findClosestTarget()
        {
            targetEntity = mob.getWorld().getClosestPlayer(predicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }

        @Override
        public boolean canStart()
        {
            if (mob.targetUUID == null && mob.getRandom().nextInt(toGoalTicks(10)) != 0)
            {
                return false;
            }
            else
            {
                this.findClosestTarget();
                return targetEntity != null;
            }
        }

        @Override
        public void start()
        {
            mob.setPersistentTarget(targetEntity);
            super.start();
        }
    }

    // Looks for the targeted player in the world based on the stored UUID
    private static class FollowerActiveTargetGoal extends Goal
    {
        private final FollowerEntity mob;

        public FollowerActiveTargetGoal(FollowerEntity mob, Class<PlayerEntity> targetClass)
        {
            this.mob = mob;
        }

        @Override
        public boolean canStart()
        {
            return (mob.getTarget() == null || !mob.getTarget().isAlive()) && mob.targetUUID != null;
        }

        @Override
        public void start()
        {
            PlayerEntity playerEntity = mob.getWorld().getPlayerByUuid(mob.targetUUID);
            if (playerEntity != null)
            {
                mob.setTarget(playerEntity);
            }
        }

        @Override
        public boolean shouldContinue()
        {
            return false;
        }
    }
}
