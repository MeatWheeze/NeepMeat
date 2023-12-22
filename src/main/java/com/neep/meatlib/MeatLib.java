package com.neep.meatlib;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.SoundRegistry;
import net.fabricmc.api.ModInitializer;

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
        BlockRegistry.init();
        ItemRegistry.init();
        SoundRegistry.init();
    }
}
