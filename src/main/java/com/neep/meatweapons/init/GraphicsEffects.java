package com.neep.meatweapons.init;

import com.mojang.serialization.Lifecycle;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.particle.BeamEffect;
import com.neep.meatweapons.particle.BulletTrailEffect;
import com.neep.meatweapons.particle.GraphicsEffect;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class GraphicsEffects
{
//    protected static Map<Identifier, BeamEffect.Factory> BEAMS = new HashMap<>();
//    private static final Object2IntMap<Identifier> entryToRawId = new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy());


    public GraphicsEffects(String defaultId, RegistryKey<? extends Registry<BeamEffect.Factory>> key, Lifecycle lifecycle)
    {
//        super(defaultId, key, lifecycle);
    }

    public static DefaultedRegistry<BeamEffect.Factory> GRAPHICS_EFFECTS = FabricRegistryBuilder.createDefaulted(BeamEffect.Factory.class,
            new Identifier(MeatWeapons.NAMESPACE, "graphics_effects"),
            new Identifier(MeatWeapons.NAMESPACE, "null")).buildAndRegister();

    public static final BeamEffect.Factory BEAM = register(MeatWeapons.NAMESPACE, "beam", BeamEffect::new);
    public static final BeamEffect.Factory BULLET_TRAIL = register(MeatWeapons.NAMESPACE, "bullet_trail", BulletTrailEffect::new);

    public static GraphicsEffect.Factory register(String namespace, String id, GraphicsEffect.Factory factory)
    {
        return Registry.register(GRAPHICS_EFFECTS, new Identifier(namespace, id), factory);
    }
}
