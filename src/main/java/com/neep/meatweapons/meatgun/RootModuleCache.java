package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;

import java.util.Map;
import java.util.UUID;

public class RootModuleCache
{
    //
    private static final Map<UUID, MeatgunModule> MAP = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, true);

    public static MeatgunModule getOrCreate(UUID uuid, Meatgun meatgun, MeatgunComponent.Listener listener)
    {
        return MAP.computeIfAbsent(uuid, u -> meatgun.createBase(listener));
    }
}
