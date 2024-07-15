package com.neep.meatlib.client;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.client.network.screen.ScreenPacketClient;
import com.neep.meatlib.graphics.client.GraphicsEffectClient;
import com.neep.meatlib.network.BlockEntitySync;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatlib.util.ValkyrienSkiesUtil;
import com.neep.neepmeat.client.item.BlockAttackListenerThings;
import net.fabricmc.api.ClientModInitializer;

public class MeatLibClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        if (MeatLib.vsUtil != null)
            MeatLib.vsUtil.CLIENT = new ValkyrienSkiesUtil.Client();

        MeatlibRecipes.initClient();
        GraphicsEffectClient.init();
        ScreenPacketClient.init();
        BlockAttackListenerThings.init();
        BlockEntitySync.Client.init();
        MWHudThings.init();
    }
}
