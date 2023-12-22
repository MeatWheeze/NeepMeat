package com.neep.neepmeat.api.enlightenment;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.network.EnlightenmentEventPacket;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EnlightenmentEventManager
{
    private int counter = 0;
    private final Random random;
    protected final List<EnlightenmentEvent> tickingEvents = new ArrayList<>(16);

    public static SimpleDefaultedRegistry<EnlightenmentEvent.Factory> EVENTS = FabricRegistryBuilder.createDefaulted(EnlightenmentEvent.Factory.class,
            new Identifier(NeepMeat.NAMESPACE, "enlightenment_event"),
            new Identifier(NeepMeat.NAMESPACE, "null")).buildAndRegister();

//    public static final EnlightenmentEvent.Factory TEST_EVENT = register(new TickingEnlightenmentEvent.Factory());

    public static void init()
    {
        ServerTickEvents.START_WORLD_TICK.register(world ->
        {
            ((IServerWorld) world).getEnlightenmentEventManager().tick(world);
        });
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
                if (EVENTS.isEmpty()) return;

                // Choose a random factory. If it can't spawn, find one that does.
                List<EnlightenmentEvent.Factory> factories = EVENTS.stream().collect(Collectors.toList());
                Collections.shuffle(factories);
                int i = 0;
                while (!factories.get(i).willSpawn(world, player))
                {
                    ++i;
                    if (i >= factories.size()) return;
                }
                EnlightenmentEvent.Factory factory = factories.get(i);
                spawnFactory(world, player, factory);
            }
        }
    }

    protected void spawnFactory(ServerWorld world, ServerPlayerEntity player, EnlightenmentEvent.Factory factory)
    {
        EnlightenmentEvent event = factory.create(world, player);
        EnlightenmentEventPacket.send(factory, world, player);
        tickingEvents.add(event);
        event.spawn();
    }
}
