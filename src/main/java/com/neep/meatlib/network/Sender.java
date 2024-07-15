package com.neep.meatlib.network;

import net.minecraft.network.PacketByteBuf;

public interface Sender<T>
{
    static <T> Sender<T> empty()
    {
        return new Sender<T>()
        {
            @Override
            public void send(PacketByteBuf buf)
            {

            }
        };
    }

    void send(PacketByteBuf buf);
}
