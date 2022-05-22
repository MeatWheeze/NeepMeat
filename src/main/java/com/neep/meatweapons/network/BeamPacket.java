package com.neep.meatweapons.network;

import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.particle.BeamEffect;
import com.neep.meatweapons.particle.GraphicsEffect;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BeamPacket
{
    public static Packet<?> create(ServerWorld world, BeamEffect.Factory factory, Vec3d start, Vec3d end, Vec3d velocity, int maxTime, Identifier packetID)
    {
        if (world.isClient)
            throw new IllegalStateException("packet create called on the client!");

        PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(GraphicsEffects.GRAPHICS_EFFECTS.getRawId(factory));

        PacketBufUtil.writeVec3d(byteBuf, start);
        PacketBufUtil.writeVec3d(byteBuf, end);
        PacketBufUtil.writeVec3d(byteBuf, velocity);
        byteBuf.writeInt(maxTime);
        byteBuf.writeIdentifier(world.getRegistryKey().getValue());
        return ServerSidePacketRegistry.INSTANCE.toPacket(packetID, byteBuf);
    }

    @Environment(value= EnvType.CLIENT)
    public static void registerReceiver()
    {
        ClientPlayNetworking.registerGlobalReceiver(MWNetwork.EFFECT_ID, (client, handler, byteBuf, responseSender) ->
        {
            GraphicsEffect.Factory factory = GraphicsEffects.GRAPHICS_EFFECTS.get(byteBuf.readVarInt());
            Vec3d start = PacketBufUtil.readVec3d(byteBuf);
            Vec3d end = PacketBufUtil.readVec3d(byteBuf);
            Vec3d velocity = PacketBufUtil.readVec3d(byteBuf);
            int maxTime = byteBuf.readInt();
            Identifier worldId = byteBuf.readIdentifier();

            client.execute(() ->
            {
                ClientWorld world;
                if ((world = MinecraftClient.getInstance().world) == null)
                    throw new IllegalStateException("Tried to spawn effect in a null world!");

                if (world.getRegistryKey().getValue().equals(worldId))
                {
                    GraphicsEffect effect = factory.create(world, start, end, velocity, maxTime);
                    GraphicsEffect.addEffect(effect);
                }
            });
        });
    }
}
