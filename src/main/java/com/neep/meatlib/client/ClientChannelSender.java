package com.neep.meatlib.client;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.network.ChannelFormatFormatImpl;
import com.neep.meatlib.network.Sender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientChannelSender<T> implements Sender<T>
{
    private final Identifier name;
    private final ChannelFormat<T> format;

    public ClientChannelSender(Identifier name, ChannelFormat<T> format)
    {
        this.name = name;
        this.format = format;
    }

    @Override
    public void send(PacketByteBuf buf)
    {
        ClientPlayNetworking.send(name, buf);
    }
}
