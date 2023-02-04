package com.neep.neepmeat.api.network;

import com.neep.meatweapons.network.MWNetwork;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.enlightenment.EnlightenmentEvent;
import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class EnlightenmentEventPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "enlightenment_evernt");

    public static void send(EnlightenmentEvent.Factory factory, ServerWorld world, ServerPlayerEntity player)
    {
        ServerPlayNetworking.send(player, ID, create(factory, world, player));
    }

    public static PacketByteBuf create(EnlightenmentEvent.Factory factory, ServerWorld world, ServerPlayerEntity player)
    {
        if (world.isClient)
            throw new IllegalStateException("packet create called on the client!");

        PacketByteBuf byteBuf = PacketByteBufs.create();
        byteBuf.writeVarInt(EnlightenmentEventManager.EVENTS.getRawId(factory));
        byteBuf.writeIdentifier(world.getRegistryKey().getValue());

        return byteBuf;
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(MWNetwork.EFFECT_ID, (client, handler, byteBuf, responseSender) ->
            {
                EnlightenmentEvent.Factory factory = EnlightenmentEventManager.EVENTS.get(byteBuf.readVarInt());
                Identifier worldId = byteBuf.readIdentifier();

                client.execute(() ->
                {
                    ClientWorld world;
                    if ((world = MinecraftClient.getInstance().world) == null)
                        throw new IllegalStateException("Tried to spawn enlightenment effect in a null world!");

                    if (world.getRegistryKey().getValue().equals(worldId))
                    {
//                        EnlightenmentEventClient.spawnEvent(factory.create(world, client.player));
                    }
                });
            });
        }
    }
}
