package com.neep.meatweapons.particle;

import com.neep.meatlib.graphics.GraphicsEffect;
import com.neep.meatlib.network.PacketBufUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class BeamGraphicsEffect implements GraphicsEffect
{
    protected UUID uuid;
    protected World world;
    protected long time;
    public boolean alive = true;
    protected Vec3d start;
    protected Vec3d end;
    protected Vec3d velocity;
    protected int maxTime;
    protected float scale;

    public BeamGraphicsEffect(World world, UUID uuid, PacketByteBuf buf)
    {
        this.world = world;
        this.uuid = uuid;
        this.start = PacketBufUtil.readVec3d(buf);
        this.end = PacketBufUtil.readVec3d(buf);
        this.velocity = PacketBufUtil.readVec3d(buf);
        this.scale = buf.readFloat();
        this.maxTime = buf.readInt();
    }

    @Override
    public World getWorld()
    {
        return world;
    }

    @Override
    public void tick()
    {
        if (!this.alive)
            return;

        if (maxTime > 0 && time > maxTime)
        {
            this.remove();
        }

        ++time;
    }

    @Override
    public boolean isRemoved()
    {
        return !alive;
    }

    public void remove()
    {
        this.alive = false;
    }


    @FunctionalInterface
    public interface Factory
    {
        BeamGraphicsEffect create(World world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime);
    }
}
