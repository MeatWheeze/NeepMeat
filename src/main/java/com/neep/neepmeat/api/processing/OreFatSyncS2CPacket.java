package com.neep.neepmeat.api.processing;

import com.neep.meatlib.network.PacketBufUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

public record OreFatSyncS2CPacket(Map<NbtCompound, OreFatRegistry.Entry> nbtToEntry)
{
    public static OreFatSyncS2CPacket fromBuf(PacketByteBuf buf)
    {
        Map<NbtCompound, OreFatRegistry.Entry> map = new HashMap<>();
        PacketBufUtil.readMap(buf, map::put, PacketByteBuf::readNbt, OreFatRegistry.Entry::read);

        return new OreFatSyncS2CPacket(map);
    }

    public void write(PacketByteBuf buf)
    {
        PacketBufUtil.writeMap(buf, nbtToEntry, (k, b) -> b.writeNbt(k), OreFatRegistry.Entry::write);
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void onPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
        {
            OreFatSyncS2CPacket packet = OreFatSyncS2CPacket.fromBuf(buf);
            client.execute(() ->
                    OreFatRegistry.INSTANCE.onPacket(packet));
        }
    }
}
