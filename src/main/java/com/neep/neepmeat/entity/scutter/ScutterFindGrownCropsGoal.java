package com.neep.neepmeat.entity.scutter;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

// For use when the scutter is already on farmland
public class ScutterFindGrownCropsGoal extends Goal
{
    private final FarmingScutter entity;
    private final World world;
    private final int interval;

    private long lastTime = 0;

    public ScutterFindGrownCropsGoal(FarmingScutter entity, int interval)
    {
        this.entity = entity;
        this.world = entity.getWorld();
        this.interval = interval;
    }

    @Override
    public void setControls(EnumSet<Control> controls)
    {
        EnumSet.of(Control.MOVE);
    }

    @Override
    public boolean canStart()
    {
        return world.getTime() - interval > lastTime &&
                entity.getTargets().isEmpty();
    }

    @Override
    public void start()
    {
        entity.addTargets(findNearest(64));
        lastTime = world.getTime();
    }

    @Override
    public boolean shouldContinue()
    {
        return false;
    }

    @Override
    public boolean canStop()
    {
        return true;
    }

    private List<BlockPos> findNearest(int maxDepth)
    {
        BlockPos origin = entity.getBlockPos();

        Queue<BlockPos> posQueue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        List<BlockPos> result = new ArrayList<>();

        posQueue.add(origin);

        int depth = 0;
        while (depth < maxDepth && !posQueue.isEmpty())
        {
            BlockPos current = posQueue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            for (Direction direction : Direction.values())
            {
                if (direction.getAxis().isVertical())
                    continue;

                mutable.set(current, direction);

                if (!visited.contains(mutable))
                {
                    visited.add(mutable.toImmutable());

                    BlockState offsetState = world.getBlockState(mutable);
                    if (FarmingScutter.isFarmland(offsetState))
                    {
//                        if (world instanceof ServerWorld serverWorld)
//                        {
//                            serverWorld.spawnParticles(ParticleTypes.COMPOSTER, mutable.getX() + 0.5, mutable.getY() + 1.5, mutable.getZ() + 0.5, 1, 0.3, 0.3, 0.3, 0.1);
//                        }
                        posQueue.add(mutable.toImmutable());
                        BlockPos up = mutable.up();
                        if (FarmingScutter.isGrownCrop(world.getBlockState(up)))
                        {
                            result.add(up);
                            return result;
                        }
                    }
                }
            }

            depth++;
        }

        return result;
    }
}
