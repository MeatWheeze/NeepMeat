package com.neep.meatlib.client;

import com.neep.meatlib.graphics.client.GraphicsEffectClient;
import com.neep.meatlib.recipe.MeatlibRecipes;
import net.fabricmc.api.ClientModInitializer;

public class MeatLibClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        MeatlibRecipes.initClient();
        GraphicsEffectClient.init();
    }
}
