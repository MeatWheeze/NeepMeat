package com.neep.meatlib;

import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.meatlib.api.event.InitialTicks;
import com.neep.meatlib.graphics.GraphicsEffects;
import com.neep.meatlib.item.MeatItemGroups;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.SoundRegistry;
import com.neep.meatlib.storage.StorageEvents;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.MinecraftServerAccess;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class MeatLib implements ModInitializer
{
    public static final String NAMESPACE = "meatlib";
    public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);
    public static String CURRENT_NAMESPACE;
    private static boolean active;

    public static void assertActive(Object object)
    {
        if (CURRENT_NAMESPACE == null)
            throw new IllegalStateException("MeatLib: Object '" + object + "' was queued for registration without a namespace");
    }

    public static Context getContext(String namespace)
    {
        return new Context(namespace);
    }

//    public static void setNamespace(String string)
//    {
//        if (active) throw new IllegalStateException();
//        CURRENT_NAMESPACE = string;
//        active = true;
//    }

    public static BlockApiLookup<Void, Void> VOID_LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_void"), Void.class, Void.class);

    @Override
    public void onInitialize()
    {
        MinecraftServerAccess.init();

        SoundRegistry.init();
        GraphicsEffects.init();
//        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MeatRecipeManager.getInstance());
        MeatlibRecipes.init();
        InitialTicks.init();
        MeatItemGroups.init();
        StorageEvents.init();
        RecipeInputs.init();

        // Things that need to access the server's data resources
        ServerLifecycleEvents.SERVER_STARTED.register(server -> DataPackPostProcess.AFTER_DATA_PACK_LOAD.invoker().event(server));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> DataPackPostProcess.AFTER_DATA_PACK_LOAD.invoker().event(server));
        DataPackPostProcess.AFTER_DATA_PACK_LOAD.addPhaseOrdering(DataPackPostProcess.FIRST, DataPackPostProcess.SECOND);

        // Synchronise after data packs are loaded/reloaded for every player on the server
        DataPackPostProcess.AFTER_DATA_PACK_LOAD.register(DataPackPostProcess.SECOND, server ->
                DataPackPostProcess.SYNC.invoker().sync(server, new HashSet<>(server.getPlayerManager().getPlayerList())));
    }

    public static class Context implements AutoCloseable
    {
        private static boolean ACTIVE;

        protected Context(String namespace)
        {
            if (ACTIVE) throw new IllegalStateException("Meatlib: " + namespace + " attempted to get context while it belongs to " + CURRENT_NAMESPACE);

            CURRENT_NAMESPACE = namespace;
            ACTIVE = true;
        }

        @Override
        public void close()
        {
            BlockRegistry.flush();
            ItemRegistry.flush();

            CURRENT_NAMESPACE = null;
            ACTIVE = false;
        }
    }
}
