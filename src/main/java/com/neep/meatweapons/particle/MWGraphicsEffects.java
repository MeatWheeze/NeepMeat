package com.neep.meatweapons.particle;

import com.neep.meatlib.graphics.GraphicsEffectType;
import com.neep.meatlib.graphics.GraphicsEffects;
import com.neep.meatlib.network.PacketBufUtil;
import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MWGraphicsEffects
{
    public static void init()
    {}


    public static final GraphicsEffectType BEAM = GraphicsEffects.register(MeatWeapons.NAMESPACE, "beam", new GraphicsEffectType());
    public static final GraphicsEffectType BULLET_TRAIL = GraphicsEffects.register(MeatWeapons.NAMESPACE, "bullet_trail", new GraphicsEffectType());

    public static void syncBeamEffect(ServerPlayerEntity player, GraphicsEffectType factory, World world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime)
    {
        if (world.isClient)
            throw new IllegalStateException("packet create called on the client!");

        PacketByteBuf byteBuf = GraphicsEffects.createPacket(factory, world);

        PacketBufUtil.writeVec3d(byteBuf, start);
        PacketBufUtil.writeVec3d(byteBuf, end);
        PacketBufUtil.writeVec3d(byteBuf, velocity);
        byteBuf.writeFloat(scale);
        byteBuf.writeInt(maxTime);

        ServerPlayNetworking.send(player, GraphicsEffects.CHANNEL_ID, byteBuf);

    }
}
