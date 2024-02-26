package com.neep.neepmeat.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FakePlayerEntity extends ServerPlayerEntity
{
    public FakePlayerEntity(MinecraftServer server, ServerWorld world, BlockPos pos)
    {
        super(server, world, new GameProfile(null, pos.toShortString() + "fakePlayer"));
    }
}
