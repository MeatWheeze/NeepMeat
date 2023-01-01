package com.neep.meatlib;

import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.SoundRegistry;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MeatLib implements ModInitializer
{
    public static final String NAMESPACE = "meatlib";
    public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);
    public static String CURRENT_NAMESPACE;
    private static boolean active;

    public static void assertActive(Object object)
    {
        if (!active) throw new IllegalStateException("MeatLib: Object '" + object + "' was queued for registration without a namespace");
    }

    public static void setNamespace(String string)
    {
        if (active) throw new IllegalStateException();
        CURRENT_NAMESPACE = string;
        active = true;
    }

    public static void flush()
    {
        if (!active) throw new IllegalStateException();

        BlockRegistry.flush();
        ItemRegistry.flush();

        CURRENT_NAMESPACE = null;
        active = false;

    }

    public static BlockApiLookup<Void, Void> VOID_LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_void"), Void.class, Void.class);

    @Override
    public void onInitialize()
    {
        SoundRegistry.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MeatRecipeManager.getInstance());
    }
}
