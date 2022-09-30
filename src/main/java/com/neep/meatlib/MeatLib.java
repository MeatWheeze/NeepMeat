package com.neep.meatlib;

import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.recipe.MeatRecipeReloadListener;
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

    public static void setNamespace(String string)
    {
        CURRENT_NAMESPACE = string;
    }

    public static BlockApiLookup<Void, Void> VOID_LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_void"), Void.class, Void.class);

    @Override
    public void onInitialize()
    {
        BlockRegistry.init();
        ItemRegistry.init();
        SoundRegistry.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MeatRecipeManager.getInstance());
    }
}
