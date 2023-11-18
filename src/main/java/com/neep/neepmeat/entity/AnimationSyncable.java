package com.neep.neepmeat.entity;

import com.google.common.collect.Queues;
import com.neep.neepmeat.network.EntityAnimationS2C;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;

import java.util.Queue;

public interface AnimationSyncable
{
    AnimationQueue getQueue();

    default void syncNearby(String name)
    {
        if (this instanceof Entity entity)
        {
            for (var player : PlayerLookup.tracking(entity))
            {
                EntityAnimationS2C.send(player, entity, name);
            }
        }
    }

    class AnimationQueue
    {
        protected Queue<String> animations = Queues.newArrayDeque();

        public void add(String name)
        {
            animations.add(name);
        }

        public boolean isEmpty()
        {
            return animations.isEmpty();
        }

        public String poll()
        {
            return animations.poll();
        }
    }
}
