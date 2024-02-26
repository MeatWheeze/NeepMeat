package com.neep.meatweapons.network;

import com.neep.meatweapons.item.GunItem;
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

public class MWAttackC2SPacket
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "weapon_trigger");

    public static final int TRIGGER_NONE = 0;
    public static final int TRIGGER_PRIMARY = 1;
    public static final int TRIGGER_SECONDARY = 2;

    public static void init()
    {

    }

    public static PacketByteBuf create(int trigger, double pitch, double yaw, int hand, ActionType actionType)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(trigger);
        buf.writeDouble(pitch);
        buf.writeDouble(yaw);
        buf.writeByte(hand);
        buf.writeByte(actionType.ordinal());

        return buf;
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, MWAttackC2SPacket::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
    {
        int triggerId = buf.readVarInt();
        double pitch = buf.readDouble();
        double yaw = buf.readDouble();
        byte hand = buf.readByte();
        ActionType actionType = ActionType.values()[buf.readByte()];

        ItemStack mainStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offStack = player.getStackInHand(Hand.OFF_HAND);

        HandType handType = HandType.values()[hand];

        switch (actionType)
        {
            case PRESS ->
            {
                if ((hand & 0b01) > 0 && mainStack.getItem() instanceof GunItem gunItem)
                {
                    gunItem.trigger(player.world, player, mainStack, triggerId, pitch, yaw, handType);
                }

                if ((hand & 0b10) > 0 && offStack.getItem() instanceof GunItem gunItem)
                {
                    gunItem.trigger(player.world, player, offStack, triggerId, pitch, yaw, handType);
                }
            }
            case RELEASE ->
            {
                if ((hand & 0b01) > 0 && mainStack.getItem() instanceof GunItem gunItem)
                {
                    gunItem.release(player.world, player, mainStack, triggerId, pitch, yaw, handType);
                }

                if ((hand & 0b10) > 0 && offStack.getItem() instanceof GunItem gunItem)
                {
                    gunItem.release(player.world, player, offStack, triggerId, pitch, yaw, handType);
                }
            }
        }

        player.meatweapons$getWeaponManager().updateStatus(handType, triggerId, actionType);
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

        public Hand oneOrTheOther()
        {
            return this == OFF ? Hand.OFF_HAND : Hand.MAIN_HAND;
        }

        public boolean mainHand()
        {
            return this == MAIN || this == BOTH;
        }

        public boolean offHand()
        {
            return this == OFF || this == BOTH;
        }
    }

    public enum ActionType
    {
        PRESS,
        RELEASE;

        public boolean pressed()
        {
            return this == PRESS;
        }

    }
}
