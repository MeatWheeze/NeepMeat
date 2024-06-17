package com.neep.neepmeat.entity.follower;

import com.neep.neepmeat.entity.LimbEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FollowerEntity extends PathAwareEntity
{
    private final List<LimbEntity> limbs = new ArrayList<>();

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
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
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

        if (getTarget() != null && distanceTo(getTarget()) < 1.4)
        {
            teleportRandomly();
        }
    }

    private void teleportRandomly()
    {
        if (!this.getWorld().isClient() && this.isAlive())
        {
            double d = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
            double e = this.getY() + (double) (this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
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
}
