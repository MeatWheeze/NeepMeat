package com.neep.meatlib.client;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.network.Receiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientChannelReceiver<T> implements Receiver<T>
{
    private final Identifier name;

    public ClientChannelReceiver(Identifier name, ChannelFormat<T> format, T listener)
    {
        this.name = name;

        ClientPlayNetworking.registerReceiver(name, (client, handler, buf, responseSender) ->
        {
            format.receive(listener, buf, client);
        });
    }

    @Override
    public void close()
    {
        ClientPlayNetworking.unregisterReceiver(name);
    }
}
