package com.neep.neepmeat.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface S2CSender
{
    static PacketByteBuf getBuf()
    {
        return PacketByteBufs.create();
    }

    static void send(PlayerEntity player, Identifier identifier, PacketByteBuf buf)
    {
        ServerPlayNetworking.send((ServerPlayerEntity) player, identifier, buf);
    }
}
