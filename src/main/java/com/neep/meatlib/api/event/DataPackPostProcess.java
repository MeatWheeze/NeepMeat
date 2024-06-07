package com.neep.meatlib.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface DataPackPostProcess
{
    Event<DataPackPostProcess> EVENT = EventFactory.createArrayBacked(DataPackPostProcess.class,
            (listeners) -> (server ->
            {
                for (var listener : listeners)
                {
                    listener.event(server);
                }
            }));

    void event(MinecraftServer server);
}
