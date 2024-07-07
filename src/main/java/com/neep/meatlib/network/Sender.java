package com.neep.meatlib.network;

import net.minecraft.network.PacketByteBuf;

public interface Sender<T>
{
    static <T> Sender<T> empty(T emptyEmitter)
    {
        return new Sender<T>()
        {
            @Override
            public T emitter()
            {
                return emptyEmitter;
            }

            @Override
            public void send(PacketByteBuf buf)
            {

            }
        };
    }

    T emitter();

    void send(PacketByteBuf buf);
}
