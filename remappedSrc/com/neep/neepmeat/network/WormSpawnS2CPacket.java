package com.neep.neepmeat.network;

import com.neep.neepmeat.entity.worm.WormEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class WormSpawnS2CPacket extends EntitySpawnS2CPacket
{
    public WormSpawnS2CPacket(int id, UUID uuid, double x, double y, double z, float pitch, float yaw, EntityType<?> entityTypeId, int entityData, Vec3d velocity)
    {
        super(id, uuid, x, y, z, pitch, yaw, entityTypeId, entityData, velocity, 0);
    }

    public WormSpawnS2CPacket(WormEntity entity)
    {
        this(
                entity.getId(),
                entity.getUuid(),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getPitch(),
                entity.getYaw(),
                entity.getType(),
                0,
                entity.getVelocity());
    }
}
