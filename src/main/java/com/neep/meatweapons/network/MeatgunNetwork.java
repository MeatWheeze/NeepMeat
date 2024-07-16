package com.neep.meatweapons.network;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.GlobalChannelManager;
import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MeatgunNetwork
{
    public static Identifier CHANNEL = new Identifier(MeatWeapons.NAMESPACE, "meatgun_recoil");

    public static final GlobalChannelManager<SendAnimation> SEND_ANIMATION = GlobalChannelManager.create(new Identifier(MeatWeapons.NAMESPACE, "meatgun_animation"),
            ChannelFormat.builder(SendAnimation.class).param(ParamCodec.STRING).param(ParamCodec.BUF).build());

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

    public interface SendAnimation
    {
        void apply(String name, PacketByteBuf buf);
    }
}
