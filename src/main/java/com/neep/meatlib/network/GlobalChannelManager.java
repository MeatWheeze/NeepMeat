package com.neep.meatlib.network;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.client.ClientChannelReceiver;
import com.neep.meatlib.client.ClientChannelSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class GlobalChannelManager<T>
{
    private final Identifier name;
    private final ChannelFormat<T> format;
    private final PlayerEntity player;

    private final List<Receiver<T>> receivers = new ArrayList<>();

    // If sources are split in the future, I may need to instantiate this through an opaque functional interface
    // that changes depending on environment.
    public static <T> GlobalChannelManager<T> create(Identifier name, ChannelFormat<T> format, PlayerEntity player)
    {
        return new GlobalChannelManager<>(name, format, player);
    }

    protected GlobalChannelManager(Identifier name, ChannelFormat<T> format, PlayerEntity player)
    {
        this.name = name;
        this.format = format;
        this.player = player;
    }

    private Sender<T> createSender(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity serverPlayerEntity)
        {
            return buf -> ServerPlayNetworking.send(serverPlayerEntity, name, buf);
        }
        else
        {
            // This should be server-safe as this will never be called on the server, but it's a bit naughty.
            return new ClientChannelSender<>(name, format);
        }
    }

    public ChannelFormat<T> format()
    {
        return format;
    }

    public T emitter(PlayerEntity to)
    {
        return format.emitter(createSender(to));
    }

    public void receiver(T listener)
    {
        Receiver<T> receiver;

        if (player instanceof ServerPlayerEntity)
        {
            receiver = new ServerChannelReceiver<>((ServerPlayerEntity) player, name, format, listener);
            receivers.add(receiver);
        }
        else
        {
            receiver = new ClientChannelReceiver<>(name, format, listener);
            receivers.add(receiver);
        }
    }

    public void close()
    {
        receivers.forEach(Receiver::close);
    }
}
