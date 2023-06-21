package com.neep.neepmeat.network;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

public class NMTrackedData
{
    public static final TrackedDataHandler<Double> DOUBLE = new TrackedDataHandler<>()
    {

        @Override
        public void write(PacketByteBuf packetByteBuf, Double d)
        {
            packetByteBuf.writeDouble(d);
        }

        @Override
        public Double read(PacketByteBuf packetByteBuf)
        {
            return (double) packetByteBuf.readFloat();
        }

        @Override
        public Double copy(Double d)
        {
            return d;
        }
    };

    public static void init()
    {
        TrackedDataHandlerRegistry.register(DOUBLE);
    }
}
