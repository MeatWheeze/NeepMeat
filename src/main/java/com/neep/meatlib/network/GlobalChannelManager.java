package com.neep.meatlib.network;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.client.ClientChannelSender;
import com.neep.meatlib.client.GlobalClientChannelReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GlobalChannelManager<T>
{
    private final Identifier name;
    private final ChannelFormat<T> format;

    // If sources are split in the future, I may need to instantiate this through an opaque functional interface
    // that changes depending on environment.
    public static <T> GlobalChannelManager<T> create(Identifier name, ChannelFormat<T> format)
    {
        return new GlobalChannelManager<>(name, format);
    }

    protected GlobalChannelManager(Identifier name, ChannelFormat<T> format)
    {
        this.name = name;
        this.format = format;
    }

    private Sender<T> createSender(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity serverPlayerEntity)
        {
            return buf -> ServerPlayNetworking.send(serverPlayerEntity, name, buf);
        }
        else
        {
            // This should be server-safe as it will never be called on the server, but it's a bit naughty.
            return new ClientChannelSender<>(name, format);
        }
    }

    public ChannelFormat<T> format()
    {
        return format;
    }

    public T emitter(PlayerEntity to)
    {
        // TODO: Look into caching senders in a map
        return format.emitter(createSender(to));
    }

    public void receiver(EnvType envType, T listener)
    {
        if (envType == EnvType.SERVER)
        {
            GlobalServerChannelReceiver.register(name, format, listener);
        }
        else
        {
            GlobalClientChannelReceiver.register(name, format, listener);
        }
    }

    public void receiverHandler(EnvType envType, ReceiveHandler<T> handler)
    {
        if (envType == EnvType.SERVER)
        {
            GlobalServerChannelReceiver.register(name, format, handler);
        }
        else
        {
            GlobalClientChannelReceiver.register(name, format, handler);
        }
    }

    public interface ReceiveHandler<T>
    {
        T receive(PlayerEntity player, PacketByteBuf buf, PacketSender responseSender);
    }
}
