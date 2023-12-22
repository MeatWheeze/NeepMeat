package com.neep.neepmeat.transport;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.blood_network.BloodNetworkChunkComponent;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;

public class TransportComponents implements ChunkComponentInitializer
{
    public static final ComponentKey<BloodNetworkChunkComponent> BLOOD_NETWORK =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "blood_network_chunk"),
                    BloodNetworkChunkComponent.class);


    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry)
    {
        registry.register(BLOOD_NETWORK, BloodNetworkChunkComponent.class, BloodNetworkChunkComponent::new);
    }
}
