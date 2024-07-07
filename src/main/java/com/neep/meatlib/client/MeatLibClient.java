package com.neep.meatlib.client;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.graphics.client.GraphicsEffectClient;
import com.neep.meatlib.network.BlockEntitySync;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.neepmeat.client.item.BlockAttackListenerThings;
import net.fabricmc.api.ClientModInitializer;

public class MeatLibClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        MeatLib.isClient = true;

        MeatlibRecipes.initClient();
        GraphicsEffectClient.init();
//        ScreenPacketClient.init();
        BlockAttackListenerThings.init();
        BlockEntitySync.Client.init();
    }
}
