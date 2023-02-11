package com.neep.meatweapons.network;

import com.neep.meatweapons.item.BaseGunItem;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class GunFireC2SPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "weapon_trigger");

    public static void init()
    {

    }

    public static PacketByteBuf create(int trigger)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(trigger);

        return buf;
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, GunFireC2SPacket::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
    {
        int id = buf.readVarInt();
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        Item item = stack.getItem();

        if (item instanceof BaseGunItem gun)
        {
            gun.trigger(player.world, player, stack, id);
        }
    }
}
