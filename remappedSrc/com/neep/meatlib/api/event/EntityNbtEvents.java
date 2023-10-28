package com.neep.meatlib.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public interface EntityNbtEvents
{
    Event<EntityNbtEvents> WRITE = EventFactory.createArrayBacked(EntityNbtEvents.class,
            (listeners) -> ((entity, nbt) ->
            {
                for (EntityNbtEvents listener : listeners)
                {
                    listener.writeNbt(entity, nbt);
                }
            }));

    Event<EntityNbtEvents> READ = EventFactory.createArrayBacked(EntityNbtEvents.class,
            (listeners) -> ((entity, nbt) ->
            {
                for (EntityNbtEvents listener : listeners)
                {
                    listener.writeNbt(entity, nbt);
                }
            }));

    void writeNbt(Entity entity, NbtCompound nbt);
}
