package com.neep.neepbus;

import com.neep.neepbus.component.NetworkingToolComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class NeepBusComponents implements ItemComponentInitializer
{
    public static final ComponentKey<NetworkingToolComponent> NETWORKING_TOOL =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepBus.NAMESPACE, "networking_tool"),
                    NetworkingToolComponent.class);

    @Override
    public void registerItemComponentFactories(@NotNull ItemComponentFactoryRegistry registry)
    {
//        registry.register(NeepBus.NETWORKING_TOOL,
    }
}
