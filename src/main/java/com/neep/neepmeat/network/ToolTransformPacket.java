package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.TransformingTools;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ToolTransformPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "key_press");

    public static void registerReceiver()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, ((server, player, handler, buf, responseSender) ->
        {
            ItemStack oldStack = player.getStackInHand(Hand.MAIN_HAND);
            TransformingTools.swap(oldStack, player);
        }));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Client
    {
        public static void send()
        {
            PacketByteBuf buf = PacketByteBufs.create();

            ClientPlayNetworking.send(ID, buf);
        }
    }
}
