package com.neep.meatlib.network;

import com.neep.meatlib.api.network.ChannelFormat;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerChannelSender<T> implements Sender<T>
{
    private final Identifier name;
    private final ChannelFormat<T> format;
    private final PlayerEntity player;

    public ServerChannelSender(Identifier name, ChannelFormat<T> format, PlayerEntity player)
    {
        this.name = name;
        this.format = format;
        this.player = player;
    }

    @Override
    public T emitter()
    {
        return format.emitter(this);
    }

    @Override
    public void send(PacketByteBuf buf)
    {
        if (player instanceof ServerPlayerEntity serverPlayerEntity)
        {
            ServerPlayNetworking.send(serverPlayerEntity, name, buf);
        }
    }
}
