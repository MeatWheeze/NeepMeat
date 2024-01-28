package com.neep.neepmeat.api.enlightenment;

import dev.architectury.event.events.common.ExplosionEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public interface EnlightenmentEvent
{
    default void spawn() {}
    void tick();

    boolean isRemoved();

    interface Factory
    {
        EnlightenmentEvent create(ServerWorld world, ServerPlayerEntity player);

        boolean willSpawn(ServerWorld world, ServerPlayerEntity player);
    }

    class SimpleFactory implements Factory
    {
        private final BiFunction<World, ServerPlayerEntity, EnlightenmentEvent> factory;
        private final Predicate predicate;

        public SimpleFactory(BiFunction<World, ServerPlayerEntity, EnlightenmentEvent> factory)
        {
            this.factory = factory;
            this.predicate = (w, p) -> true;
        }

        public SimpleFactory(BiFunction<World, ServerPlayerEntity, EnlightenmentEvent> factory, Predicate predicate)
        {
            this.factory = factory;
            this.predicate = predicate;
        }

        @Override
        public EnlightenmentEvent create(ServerWorld world, ServerPlayerEntity player)
        {
            return factory.apply(world, player);
        }

        @Override
        public boolean willSpawn(ServerWorld world, ServerPlayerEntity player)
        {
            return predicate.test(world, player);
        }

        @FunctionalInterface
        private interface Predicate
        {
            boolean test(World world, ServerPlayerEntity player);
        }
    }
}
