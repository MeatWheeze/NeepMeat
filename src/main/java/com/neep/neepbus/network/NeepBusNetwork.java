package com.neep.neepbus.network;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.GlobalChannelManager;
import com.neep.neepbus.NeepBus;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class NeepBusNetwork
{
    public static final ParamCodec<BlockPos> BLOCK_POS_PARAM_CODEC = ParamCodec.of(BlockPos.class, (o, buf) -> buf.writeBlockPos(o), PacketByteBuf::readBlockPos);

    public static final GlobalChannelManager<NetworkingToolConnect> NT_CONNECT = GlobalChannelManager.create(
            new Identifier(NeepBus.REGISTRY_NAMESPACE, "nt_connect"),
            ChannelFormat.builder(NetworkingToolConnect.class)
                    .param(BLOCK_POS_PARAM_CODEC)
                    .param(ParamCodec.BOOLEAN)
                    .param(ParamCodec.INT)
                    .param(ParamCodec.STRING)
                    .build());


    @FunctionalInterface
    public interface NetworkingToolConnect
    {
        void send(BlockPos pos, boolean isOutput, int entryIndex, String address);
    }
}
