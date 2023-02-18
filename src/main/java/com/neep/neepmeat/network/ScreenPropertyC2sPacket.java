package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ScreenPropertyC2sPacket
{

    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "screen_int_update");
    public static PacketByteBuf create(int id, int value)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(id);
        buf.writeVarInt(value);

        return buf;
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, ScreenPropertyC2sPacket::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
    {
        int id = buf.readVarInt();
        int value = buf.readVarInt();
        player.currentScreenHandler.setProperty(id, value);
    }
}
