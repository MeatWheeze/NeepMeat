package com.neep.meatlib.api.event;

import com.neep.meatlib.MeatLib;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

@FunctionalInterface
public interface DataPackPostProcess
{
    Identifier FIRST = new Identifier(MeatLib.NAMESPACE, "first");
    Identifier SECOND = new Identifier(MeatLib.NAMESPACE, "second");

    Event<DataPackPostProcess> AFTER_DATA_PACK_LOAD = EventFactory.createArrayBacked(DataPackPostProcess.class,
            (listeners) -> (server ->
            {
                for (var listener : listeners)
                {
                    listener.event(server);
                }
            }));

    Event<SyncToPlayers> SYNC = EventFactory.createArrayBacked(SyncToPlayers.class,
            (listeners) -> ((server, players) ->
            {
                for (var listener : listeners)
                {
                    listener.sync(server, players);
                }
            }));

    void event(MinecraftServer server);

    @FunctionalInterface
    interface SyncToPlayers
    {
        void sync(MinecraftServer server, Set<ServerPlayerEntity> players);
    }
}
