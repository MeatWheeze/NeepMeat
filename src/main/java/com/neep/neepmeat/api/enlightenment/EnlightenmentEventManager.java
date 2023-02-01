package com.neep.neepmeat.api.enlightenment;

import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EnlightenmentEventManager
{
    private int counter = 0;
    private final Random random;
    protected final List<EnlightenmentEvent> tickingEvents = new ArrayList<>(16);

    protected static final List<EnlightenmentEvent.Factory> FACTORIES = new ArrayList<>();

    public static final EnlightenmentEvent.Factory TEST_EVENT = register(new TestEnlightenmentEvent.Factory());

    public static void init()
    {
        ServerTickEvents.START_WORLD_TICK.register(world ->
        {
            ((IServerWorld) world).getEnlightenmentEventManager().tick(world);
        });
    }

    public static EnlightenmentEvent.Factory register(EnlightenmentEvent.Factory factory)
    {
        FACTORIES.add(factory);
        return factory;
    }

    public EnlightenmentEventManager()
    {
        this.random = new Random();
    }

    protected void tick(ServerWorld world)
    {
        ++counter;

        if (counter >= 20)
        {
            chooseEvent(world);
            counter  = 0;
        }
        tickEvents();
    }

    protected void tickEvents()
    {
        tickingEvents.removeIf(EnlightenmentEvent::isRemoved);
        tickingEvents.forEach(EnlightenmentEvent::tick);
    }

    protected void chooseEvent(ServerWorld world)
    {
        for (ServerPlayerEntity player : world.getPlayers())
        {
            float p = EnlightenmentUtil.getEventProbability(player);
            random.setSeed(world.getTime());
            if (random.nextDouble() < p)
            {
                if (FACTORIES.isEmpty()) return;

                // Choose a random factory. If it can't spawn, find one that does.
                Collections.shuffle(FACTORIES);
                int i = 0;
                while (!FACTORIES.get(i).willSpawn(this, world, player))
                {
                    ++i;
                    if (i >= FACTORIES.size()) return;
                }
                EnlightenmentEvent.Factory factory = FACTORIES.get(i);
                spawnFactory(world, player, factory);
            }
        }
    }

    protected void spawnFactory(ServerWorld world, ServerPlayerEntity player, EnlightenmentEvent.Factory factory)
    {
        EnlightenmentEvent event = factory.create(this, world, player);
        tickingEvents.add(event);
        event.spawn();
    }
}
