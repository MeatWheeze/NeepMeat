package com.neep.neepmeat.transport.network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SyncRequesterScreenS2CPacket
{
    public static final Identifier SYNC_ID = new Identifier(NeepMeat.NAMESPACE, "requester_screen_sync");
    public static final Identifier REQUEST_ID = new Identifier(NeepMeat.NAMESPACE, "requester_request");

    public static PacketByteBuf encodeSync(PacketByteBuf buf, List<ResourceAmount<ItemVariant>> list)
    {
        buf.writeVarInt(list.size());
        list.forEach(a ->
        {
            a.resource().toPacket(buf);
            buf.writeVarLong(a.amount());
        });

        return buf;
    }

    public static void decodeSync(PacketByteBuf buf, List<ResourceAmount<ItemVariant>> list)
    {
        list.clear();
        int size = buf.readVarInt();

        for (int i = 0; i < size; ++i)
        {
            ItemVariant variant = ItemVariant.fromPacket(buf);
            long amount = buf.readVarLong();
            list.add(new ResourceAmount<>(variant, amount));
        }

    }

    public static PacketByteBuf encodeRequest(PacketByteBuf buf, ResourceAmount<ItemVariant> ra)
    {
        buf.writeVarLong(ra.amount());
        ra.resource().toPacket(buf);
        return buf;
    }

    public static ResourceAmount<ItemVariant> decodeRequest(PacketByteBuf buf)
    {
        long amount = buf.readVarLong();
        ItemVariant variant = ItemVariant.fromPacket(buf);
        return new ResourceAmount<>(variant, amount);
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ID, (server, player, handler, buf, responseSender) ->
        {
            if (player.currentScreenHandler instanceof ItemRequesterScreenHandler screenHandler)
            {
                PacketByteBuf buf2 = PacketByteBufs.copy(buf);
                server.execute(() ->
                {
                        screenHandler.receiveRequestPacket(buf2);
                });
            }
        });
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(SYNC_ID, (client, handler, buf, responseSender) ->
            {
                if (client.player.currentScreenHandler instanceof ItemRequesterScreenHandler screenHandler)
                {
                    screenHandler.receivePacket(buf);
                }
            });
        }
    }
}
