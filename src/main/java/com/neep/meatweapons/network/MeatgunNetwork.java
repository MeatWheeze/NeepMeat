package com.neep.meatweapons.network;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MeatgunNetwork
{
    public static Identifier CHANNEL = new Identifier(MeatWeapons.NAMESPACE, "meatgun_recoil");

    public static void sendRecoil(ServerPlayerEntity player, RecoilDirection direction, float amount, float horAmount, float returnSpeed, float horReturnSpeed)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(direction.ordinal());
        buf.writeFloat(amount);
        buf.writeFloat(horAmount);
        buf.writeFloat(returnSpeed);
        buf.writeFloat(horReturnSpeed);

        ServerPlayNetworking.send(player, CHANNEL, buf);
    }

    public enum RecoilDirection
    {
        UP,
        DOWN,
        BACK,
        FORWARDS
    }
}
