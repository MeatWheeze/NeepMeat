package com.neep.meatlib;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.particle.SuspendParticle;
import org.apache.logging.log4j.Level;

public class MeatLib implements ModInitializer
{
    public static String CURRENT_NAMESPACE;

    public static void setNamespace(String string)
    {
        CURRENT_NAMESPACE = string;
    }

    @Override
    public void onInitialize()
    {
        BlockRegistry.registerBlocks();
        ItemRegistry.registerItems();
    }
}
