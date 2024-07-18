package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.item.meatgun.Meatgun;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;

import java.util.Map;
import java.util.UUID;

public class RootModuleCache
{
    // Caching allows the module tree to outlive the item component which is replaced each time the ItemStack syncs.
    // This means that non-persistent values such as cooldowns do not have to be saved to NBT.
    private static final Map<UUID, RootModuleHolder> SERVER_MAP = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, true);
    private static final Map<UUID, RootModuleHolder> CLIENT_MAP = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, true);

    public static RootModuleHolder getOrCreate(UUID uuid, Meatgun meatgun, boolean isServer)
    {
        return isServer ?
                SERVER_MAP.computeIfAbsent(uuid, u -> new RootModuleHolder(uuid, meatgun))
                : CLIENT_MAP.computeIfAbsent(uuid, u -> new RootModuleHolder(uuid, meatgun));
    }

    public static void init()
    {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER_MAP.clear());
    }
}
