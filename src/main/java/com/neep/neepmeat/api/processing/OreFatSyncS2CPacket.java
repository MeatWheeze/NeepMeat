package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

public record OreFatSyncS2CPacket(Map<NbtCompound, OreFatRegistry.Entry> nbtToEntry) implements FabricPacket
{
    public static OreFatSyncS2CPacket fromBuf(PacketByteBuf buf)
    {
        Map<NbtCompound, OreFatRegistry.Entry> map = new HashMap<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; ++i)
        {
            NbtCompound key = buf.readNbt();
            OreFatRegistry.Entry value = OreFatRegistry.Entry.CODEC.parse(NbtOps.INSTANCE, buf.readNbt())
                    .resultOrPartial(NeepMeat.LOGGER::error).orElseThrow();
            map.put(key, value);
        }

        return new OreFatSyncS2CPacket(map);
    }

    @Override
    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(nbtToEntry.size());
        nbtToEntry.forEach((key, value) ->
        {
            buf.writeNbt(key);
            buf.writeNbt((NbtCompound) OreFatRegistry.Entry.CODEC.encode(value, NbtOps.INSTANCE, new NbtCompound())
                    .resultOrPartial(NeepMeat.LOGGER::error).orElseThrow());
        });
    }

    @Override
    public PacketType<?> getType()
    {
        return OreFatRegistry.SYNC_TYPE;
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void onPacket(OreFatSyncS2CPacket t, ClientPlayerEntity clientPlayerEntity, PacketSender packetSender)
        {
            OreFatRegistry.INSTANCE.onPacket(t);
        }
    }
}
