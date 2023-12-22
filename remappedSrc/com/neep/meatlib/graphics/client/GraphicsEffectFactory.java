package com.neep.meatlib.graphics.client;

import com.neep.meatlib.graphics.GraphicsEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.UUID;

@FunctionalInterface
public interface GraphicsEffectFactory
{
    GraphicsEffect create(World world, UUID uuid, PacketByteBuf buf);
}
