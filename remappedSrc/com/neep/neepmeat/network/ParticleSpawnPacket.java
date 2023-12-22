package com.neep.neepmeat.network;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

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
