package com.neep.meatlib.client;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.network.GlobalChannelManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GlobalClientChannelReceiver
{
    public static <T> void register(Identifier name, ChannelFormat<T> format, T listener)
    {
        ClientPlayNetworking.registerGlobalReceiver(name, (client, handler, buf, responseSender) ->
                format.receive(listener, buf, client));
    }

    public static <T> void register(Identifier name, ChannelFormat<T> format, GlobalChannelManager.ReceiveHandler<T> receiveHandler)
    {
        ClientPlayNetworking.registerGlobalReceiver(name, (client, handler, buf, responseSender) ->
                format.receive(receiveHandler.receive(client.player, buf, responseSender), buf, client));
    }
}