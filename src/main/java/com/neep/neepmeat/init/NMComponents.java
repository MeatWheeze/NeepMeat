package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.player.implant.PlayerImplantManager;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class NMComponents implements EntityComponentInitializer
{
    public static final ComponentKey<PlayerImplantManager> IMPLANT_MANAGER =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "implant_manager"),
                    PlayerImplantManager.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        registry.registerForPlayers(IMPLANT_MANAGER, PlayerImplantManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}
