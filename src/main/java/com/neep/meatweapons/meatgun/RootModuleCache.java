package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.item.meatgun.Meatgun;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;

import java.util.Map;
import java.util.UUID;

public class RootModuleCache
{
    // Caching allows the module tree to outlive the item component which is replaced each time the ItemStack syncs.
    private static final Map<UUID, RootModuleHolder> MAP = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, true);

    public static RootModuleHolder getOrCreate(UUID uuid, Meatgun meatgun)
    {
        return MAP.computeIfAbsent(uuid, u -> new RootModuleHolder(uuid, meatgun));
    }
}
