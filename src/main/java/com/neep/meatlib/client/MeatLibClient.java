package com.neep.meatlib.client;

import com.neep.meatlib.network.SyncMeatRecipesS2CPacket;
import net.fabricmc.api.ClientModInitializer;

public class MeatLibClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        SyncMeatRecipesS2CPacket.Client.registerReceiver();
    }
}
