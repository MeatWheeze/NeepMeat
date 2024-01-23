package com.neep.meatlib.storage;

import com.neep.meatlib.mixin.InventoryStorageAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class StorageEvents
{
    public static void init()
    {
        // The map uses strong keys which are not cleared when the server dies, This means that Inventories that are
        // BlockEntities prevent GC of their worlds, which causes the accumulation of CCA components and WorldChunks
        // that they reference.
        // It's very naughty, but should fix the leak with no side effects.
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
        {
            InventoryStorageAccessor.getWRAPPERS().clear();
        });
    }
}
