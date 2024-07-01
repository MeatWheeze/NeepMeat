package com.neep.meatlib.network;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.blockentity.BlockEntityClientSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockEntitySync
{
    private static final Identifier CHANNEL_ID = new Identifier(MeatLib.NAMESPACE, "block_entity_sync");

    public static <T extends BlockEntity & BlockEntityClientSerializable> void send(List<ServerPlayerEntity> players, T be)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        NbtCompound nbt = be.createSyncNbt();

        buf.writeBlockPos(be.getPos());
        buf.writeRegistryValue(Registry.BLOCK_ENTITY_TYPE, be.getType());
        buf.writeNbt(nbt);

        players.forEach(p -> ServerPlayNetworking.send(p, CHANNEL_ID, buf));
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            ClientPlayNetworking.registerGlobalReceiver(CHANNEL_ID, Client::receive);
        }

        private static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
        {
            BlockPos blockPos = buf.readBlockPos();
            BlockEntityType<?> type = buf.readRegistryValue(Registry.BLOCK_ENTITY_TYPE);
            @Nullable NbtCompound nbt = buf.readNbt();

            client.execute(() ->
            {
                client.world.getBlockEntity(blockPos, type).ifPresent(blockEntity ->
                {
                    if (nbt != null && blockEntity instanceof BlockEntityClientSerializable becs)
                    {
                        becs.fromClientTag(nbt);
                        becs.onReceiveNbt(nbt);
                    }
                });
            });
        }
    }
}