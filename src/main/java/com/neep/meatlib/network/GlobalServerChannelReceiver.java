package com.neep.meatlib.network;

import com.neep.meatlib.api.network.ChannelFormat;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class GlobalServerChannelReceiver
{
    public static <T> void register(Identifier channelName, ChannelFormat<T> format, T listener)
    {
        ServerPlayNetworking.registerGlobalReceiver(channelName, (server, player, handler, buf, responseSender) ->
                format.receive(listener, buf, server));
    }

    public static <T> void register(Identifier channelName, ChannelFormat<T> format, GlobalChannelManager.ReceiveHandler<T> receiveHandler)
    {
        ServerPlayNetworking.registerGlobalReceiver(channelName, (server, player, handler, buf, responseSender) ->
                format.receive(receiveHandler.receive(player, buf, responseSender), buf, server));
    }
}
