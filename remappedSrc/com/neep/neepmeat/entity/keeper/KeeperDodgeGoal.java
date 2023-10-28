package com.neep.neepmeat.entity.keeper;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class KeeperDodgeGoal extends Goal
{
    protected long lastDodge;
    protected float range;
    protected int ticks;
    protected final KeeperEntity entity;
    protected final int maxTicks = 20;

    public KeeperDodgeGoal(KeeperEntity entity, float range)
    {
//        super(mob, speed, false);
        this.entity = entity;
        this.range = range;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        return
                entity.getAttacker() != null
                && entity.isPlayerStaring(entity.getAttacker())
                && entity.getWorld().getTime() > lastDodge + maxTicks;
    }

    @Override
    public void start()
    {
        super.start();
        lastDodge = entity.getWorld().getTime();
        ticks = 0;
    }

    @Override
    public void stop()
    {
        super.stop();
//        entity.getMoveControl().strafeTo(-2f, entity.getRandom().nextBoolean() ? 4f : -4f);
    }

    @Override
    public void tick()
    {
        super.tick();

        LivingEntity target = entity.getTarget();
        if (ticks == 0 && target != null)
        {
            boolean b1 = entity.getRandom().nextBoolean();

            float angle = (float) (b1 ? -Math.PI / 2 : Math.PI / 2);
            Vec3d v = target.getPos().subtract(entity.getPos()).rotateY(angle);
            Vec3d newPos = findTeleportPos(entity.world, target.getPos().add(v));

            if (entity.world instanceof ServerWorld serverWorld)
            {
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.NETHER_WART_BLOCK.getDefaultState()),
                        entity.getX(), entity.getBodyY(0.5), entity.getZ(), 20, 0.1, 1, 0.1,2);
            }
            entity.teleport(newPos.x, newPos.y, newPos.z);
        }
        ++ticks;
    }

    protected static Vec3d findTeleportPos(World world, Vec3d v)
    {
        BlockPos.Mutable mutable = BlockPos.ofFloored(v).mutableCopy();
        while (world.getBlockState(mutable).getMaterial().blocksMovement())
        {
            mutable.set(mutable, Direction.UP);
        }
        return new Vec3d(v.x, mutable.getY(), v.z);
    }

    @Override
    public boolean shouldContinue()
    {
        return false;
    }
}
