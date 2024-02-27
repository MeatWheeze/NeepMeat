package com.neep.neepmeat.network;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ParticleSpawnS2C
{
    public static final Identifier PARTICLE_SPAWN = new Identifier(NeepMeat.NAMESPACE, "particle_spawn");

    public static <T extends ParticleEffect> void sendNearby(ServerWorld world, BlockPos pos, T particleType, Vec3d origin, Vec3d velocity, Vec3d spread, int count)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, 32))
        {
            send(player, particleType, origin, velocity, spread, count);
        }
    }

    public static <T extends ParticleEffect> void send(ServerPlayerEntity player, T particle, Vec3d origin, Vec3d velocity, Vec3d spread, int count)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        PacketBufUtil.writeVec3d(buf, origin);
        PacketBufUtil.writeVec3d(buf, velocity);
        PacketBufUtil.writeVec3d(buf, spread);
        buf.writeInt(count);

        buf.writeRegistryValue(Registries.PARTICLE_TYPE, particle.getType());
        particle.write(buf);

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

                ParticleType<?> type = buf.readRegistryValue(Registries.PARTICLE_TYPE);
                if (type != null)
                {
                    ParticleEffect effect = readParameters(type, buf);

                    for (int i = 0; i < count; ++i)
                    {
                        double px = origin.x + (Math.random() - 0.5) * 2 * spread.x;
                        double py = origin.y + (Math.random() - 0.5) * 2 * spread.y;
                        double pz = origin.z + (Math.random() - 0.5) * 2 * spread.z;
                        client.world.addParticle(effect, px, py, pz, velocity.x, velocity.y, velocity.z);
                    }
                }
            });
        }

        private static <T extends ParticleEffect> T readParameters(ParticleType<T> type, PacketByteBuf buf)
        {
            return type.getParametersFactory().read(type, buf);
        }
    }
}
