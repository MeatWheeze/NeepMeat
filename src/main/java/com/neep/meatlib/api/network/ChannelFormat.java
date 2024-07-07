package com.neep.meatlib.api.network;

import com.neep.meatlib.network.ChannelFormatFormatImpl;
import com.neep.meatlib.network.Sender;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.Executor;

public interface ChannelFormat<T>
{
    String APPLY_METHOD_NAME = "apply";

    static <T> ChannelFormatFormatImpl.Builder<T> builder(Class<T> clazz)
    {
        return new ChannelFormatFormatImpl.Builder<>(clazz);
    }

    void receive(T listener, PacketByteBuf buf, Executor executor);

    T emitter(Sender<T> sender);
}
