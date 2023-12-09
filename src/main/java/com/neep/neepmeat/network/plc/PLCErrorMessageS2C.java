package com.neep.neepmeat.network.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PLCErrorMessageS2C
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_error");

    public static void send(ServerPlayerEntity player, String message)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString(message);

        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
            {
                if (client.world == null || client.player == null)
                    return;

                String message = buf.readString();

                client.execute(() ->
                {
                    client.player.sendMessage(Text.of(message)); // Will not be visible inside robot. Oh well.
                    client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.ERROR, 1.0f));
                });
            });
        }
    }
}
