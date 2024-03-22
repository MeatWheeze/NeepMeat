package com.neep.meatlib.api.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public final class InitialTicks
{
    private static final HashMap<ServerWorld, InitialTicks> MAP = new HashMap<>(4);
    private final Queue<InitialTickListener> queue = new ArrayDeque<>();
    private final ServerWorld world;

    private InitialTicks(ServerWorld world)
    {
        this.world = world;
    }

    public static InitialTicks getInstance(ServerWorld world)
    {
        return MAP.computeIfAbsent(world, InitialTicks::new);
    }

    public void queue(InitialTickListener listener)
    {
        queue.add(listener);
    }

    public static void init()
    {
//        ServerWorldEvents.LOAD.register((server, world) ->
//        {
//            MAP.put(world, new InitialTicks(world));
//        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
        {
            MAP.clear();
        });

        ServerTickEvents.START_WORLD_TICK.register(world ->
        {
            InitialTicks instance = getInstance(world);
            instance.tick();
        });
    }

    private void tick()
    {
        while (!queue.isEmpty())
        {
            queue.poll().load(world);
        }
    }

    public interface InitialTickListener
    {
        void load(ServerWorld world);
    }
}
