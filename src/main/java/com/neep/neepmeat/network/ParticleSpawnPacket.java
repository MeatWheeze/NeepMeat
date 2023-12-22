package com.neep.neepmeat.network;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ParticleSpawnPacket
{
    public static final Identifier PARTICLE_SPAWN = new Identifier(NeepMeat.NAMESPACE, "particle_spawn");

    public static void send(ServerPlayerEntity player, ParticleType<?> particle, Vec3d origin, Vec3d velocity, Vec3d spread, int count)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        PacketBufUtil.writeVec3d(buf, origin);
        PacketBufUtil.writeVec3d(buf, velocity);
        PacketBufUtil.writeVec3d(buf, spread);
        buf.writeInt(count);
        buf.writeIdentifier(Registry.PARTICLE_TYPE.getId(particle));

        ServerPlayNetworking.send(player, PARTICLE_SPAWN, buf);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(PARTICLE_SPAWN, (client, handler, buf, responseSender) ->
            {
                Vec3d origin = PacketBufUtil.readVec3d(buf);
                Vec3d velocity = PacketBufUtil.readVec3d(buf);
                Vec3d spread = PacketBufUtil.readVec3d(buf);
                int count = buf.readInt();
                Identifier id = buf.readIdentifier();

                // Not sure about this
                ParticleEffect type = (ParticleEffect) Registry.PARTICLE_TYPE.get(id);

                for (int i = 0; i < count; ++i)
                {
                    double px = origin.x + (Math.random() - 0.5) * 2 * spread.x;
                    double py = origin.y + (Math.random() - 0.5) * 2 * spread.y;
                    double pz = origin.z + (Math.random() - 0.5) * 2 * spread.z;
                    client.world.addParticle(type, px, py, pz, velocity.x, velocity.y, velocity.z);
                }
            });
        }
    }
}
