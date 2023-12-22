package com.neep.meatlib.recipe;

import com.neep.meatlib.MeatLib;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;

public class MeatRecipeReloadListener implements SimpleSynchronousResourceReloadListener
{
    private static final MeatRecipeReloadListener INSTANCE = new MeatRecipeReloadListener();

    public static MeatRecipeReloadListener getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(MeatLib.NAMESPACE, "recipe");
    }

    @Override
    public void reload(ResourceManager manager)
    {
        for(Identifier id : manager.findResources("recipes", path -> path.endsWith(".json")))
        {
            try(InputStream stream = manager.getResource(id).getInputStream())
            {
//                Reader reader = new InputStreamReader(stream);
//                JsonElement rootElement = JsonParser.parseReader(reader);
            }
            catch(Exception e)
            {
                MeatLib.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        }
    }
}
