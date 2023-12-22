package com.neep.meatweapons.network;

import com.neep.meatweapons.item.IGunItem;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

    public static PacketByteBuf create(int trigger, double pitch, double yaw, int hand)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(trigger);
        buf.writeDouble(pitch);
        buf.writeDouble(yaw);
        buf.writeByte(hand);

        return buf;
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, GunFireC2SPacket::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
    {
        int id = buf.readVarInt();
        double pitch = buf.readDouble();
        double yaw = buf.readDouble();
        byte hand = buf.readByte();

        ItemStack mainStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offStack = player.getStackInHand(Hand.OFF_HAND);

        HandType handType = HandType.values()[hand];

        if ((hand & 0b01) > 0 && mainStack.getItem() instanceof IGunItem gunItem)
        {
            gunItem.trigger(player.world, player, mainStack, id, pitch, yaw, handType);
        }

        if ((hand & 0b10) > 0 && offStack.getItem() instanceof IGunItem gunItem)
        {
            gunItem.trigger(player.world, player, offStack, id, pitch, yaw, handType);
        }

//        if (item instanceof IGunItem gun)
//        {
//            gun.trigger(player.world, player, mainStack, id, pitch, yaw);
//        }
    }

    public enum HandType
    {
        NONE(0b00),
        MAIN(0b01),
        OFF(0b10),
        BOTH(0b11);

        HandType(int binary)
        {

        }
    }
}
