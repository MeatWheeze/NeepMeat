package com.neep.meatweapons.particle;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MWParticles
{
//    public static final DefaultParticleType BEAM_PARTICLE = register(MeatWeapons.NAMESPACE, "beam", FabricParticleTypes.simple());
    public static DefaultParticleType BEAM_PARTICLE = FabricParticleTypes.simple();

    public static void register()
    {
        BEAM_PARTICLE = register(MeatWeapons.NAMESPACE, "beam", BEAM_PARTICLE);
    }

    public static DefaultParticleType register(String namespace, String id, DefaultParticleType particleType)
    {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(namespace, id), particleType);
    }
}
