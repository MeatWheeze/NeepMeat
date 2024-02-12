package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ScreenPropertyC2SPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "screen_int_update");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, ScreenPropertyC2SPacket::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
    {
        int id = buf.readVarInt();
        int value = buf.readVarInt();
        var handler = player.currentScreenHandler;
        if (handler != null)
        {
            handler.setProperty(id, value);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void send(int id, int value)
        {
            ClientPlayNetworking.send(ID, create(id, value));
        }

        public static PacketByteBuf create(int id, int value)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(id);
            buf.writeVarInt(value);

            return buf;
        }
    }
}
