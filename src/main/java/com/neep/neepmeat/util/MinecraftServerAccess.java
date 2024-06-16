package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

//@Cursed
public class MinecraftServerAccess
{
    @Nullable private static MinecraftServer SERVER;

    public static void init()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                SERVER = server);

        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                SERVER = null);
    }

    @Nullable
    public static MinecraftServer get()
    {
        return SERVER;
    }
}
