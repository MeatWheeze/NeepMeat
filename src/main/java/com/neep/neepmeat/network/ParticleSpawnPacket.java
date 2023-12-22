package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ParticleSpawnPacket
{
    public static final Identifier PARTICLE_SPAWN = new Identifier(NeepMeat.NAMESPACE, "particle_spawn");

    public static void send(ServerPlayerEntity player, ParticleType<?> particle, BlockPos pos, int amount)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(pos);
        buf.writeInt(amount);
        buf.writeIdentifier(Registry.PARTICLE_TYPE.getId(particle));

        ServerPlayNetworking.send(player, PARTICLE_SPAWN, buf);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
    //        ClientPlayNetworking.registerGlobalReceiver(PARTICLE_SPAWN, (client, handler, buf, responseSender) ->
    //        {
    //            BlockPos pos = buf.readBlockPos();
    //            int amount = buf.readInt();
    //            Identifier id = buf.readIdentifier();
    //
    //            ParticleUtil.spawnParticle(client.world, pos, (ParticleEffect) Registry.PARTICLE_TYPE.get(id), UniformIntProvider.create(0, 1));
    //        });
        }
    }
}
