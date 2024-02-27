package com.neep.meatweapons.network;

import com.neep.meatlib.network.PacketBufUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class ProjectileSpawnPacket
{
    public static Packet<ClientPlayPacketListener> create(Entity e, Identifier packetID)
    {
        if (e.getWorld().isClient)
            throw new IllegalStateException("SpawnPacketUtil.create called on the logical client!");

        PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(Registries.ENTITY_TYPE.getRawId(e.getType()));
        byteBuf.writeUuid(e.getUuid());
        byteBuf.writeVarInt(e.getId());

        PacketBufUtil.writeVec3d(byteBuf, e.getPos());
        PacketBufUtil.writeAngle(byteBuf, e.getPitch());
        PacketBufUtil.writeAngle(byteBuf, e.getYaw());
//        return ServerSidePacketRegistry.INSTANCE.toPacket(packetID, byteBuf);
        return ServerPlayNetworking.createS2CPacket(packetID, byteBuf);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(MWNetwork.SPAWN_ID, (client, handler, byteBuf, responseSender) ->
            {
                EntityType<?> et = Registries.ENTITY_TYPE.get(byteBuf.readVarInt());
                UUID uuid = byteBuf.readUuid();
                int entityId = byteBuf.readVarInt();
                Vec3d pos = PacketBufUtil.readVec3d(byteBuf);
                float pitch = PacketBufUtil.readAngle(byteBuf);
                float yaw = PacketBufUtil.readAngle(byteBuf);
                client.execute(() ->
                {
                    if (MinecraftClient.getInstance().world == null)
                        throw new IllegalStateException("Tried to spawn entity in a null world!");
                    Entity e = et.create(MinecraftClient.getInstance().world);
                    if (e == null)
                        throw new IllegalStateException("Failed to create instance of entity \"" + Registries.ENTITY_TYPE.getId(et) + "\"!");
                    e.updateTrackedPosition(pos.z, pos.y, pos.z);
                    e.setPos(pos.x, pos.y, pos.z);
                    e.setPitch(pitch);
                    e.setYaw(yaw);
                    e.setId(entityId);
                    e.setUuid(uuid);
                    MinecraftClient.getInstance().world.addEntity(entityId, e);
                });
            });
        }
    }
}
