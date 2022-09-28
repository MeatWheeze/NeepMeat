package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.pylon.PylonBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSoundPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "block_sound");

    public static void send(ServerWorld world, BlockPos pos)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(pos);
//        buf.writeLong(amount);
//        buf.writeNbt(resource.toNbt());

        world.getPlayers().forEach(p -> ServerPlayNetworking.send(p, ID, buf));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
            {
                BlockPos pos = buf.readBlockPos();
                World world = MinecraftClient.getInstance().world;
                if (world.getBlockEntity(pos) instanceof PylonBlockEntity be)
                {
//                    MinecraftClient.getInstance().getSoundManager().play(new PylonSoundInstance(be, pos, NMSounds.AIRTRUCK_RUNNING, SoundCategory.BLOCKS));
                }
            });
        }
    }
}
