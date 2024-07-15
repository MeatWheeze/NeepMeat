package com.neep.meatweapons.network;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class MeatgunModuleNetwork
{
    public static final Identifier CHANNEL_ID = new Identifier(MeatWeapons.NAMESPACE, "meatgun_module");

    public static void send(ServerPlayerEntity player, PacketByteBuf buf)
    {
        ServerPlayNetworking.send(player, CHANNEL_ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            ClientPlayNetworking.registerGlobalReceiver(CHANNEL_ID, Client::apply);
        }

        private static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
        {
            if (client.player == null) // Not sure why this might happen
                return;

            UUID meatgunUuid = buf.readUuid();
            UUID componentUuid = buf.readUuid();

            PacketByteBuf copy = PacketByteBufs.copy(buf.copy());
            client.execute(() ->
            {
                for (int i = 0; i < client.player.getInventory().size(); ++i)
                {
                    ItemStack stack = client.player.getInventory().getStack(i);
                    MeatgunComponent component = MWComponents.MEATGUN.getNullable(stack);
                    if (component != null && component.getUuid().equals(meatgunUuid))
                    {
                        MeatgunModule module = component.find(componentUuid);
                        if (module != null)
                        {
                            module.receivePacket(copy);
                        }
                        break;
                    }
                }
            });
        }
    }
}
