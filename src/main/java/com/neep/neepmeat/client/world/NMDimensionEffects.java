package com.neep.neepmeat.client.world;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.mixin.DimensionEffectsAccessor;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;

public class NMDimensionEffects
{
    public static final Identifier DUAT_ID = new Identifier(NeepMeat.NAMESPACE, "duat");
    public static final DimensionEffects DUAT = new DuatDimensionEffects();

    public static void init()
    {
        register(DUAT_ID, DUAT);
    }

    public static DimensionEffects register(Identifier id, DimensionEffects effects)
    {
        DimensionEffectsAccessor.getPropertiesMap().put(id, effects);
        return effects;
    }
}
