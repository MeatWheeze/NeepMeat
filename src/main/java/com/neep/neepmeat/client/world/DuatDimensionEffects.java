package com.neep.neepmeat.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

@Environment(value= EnvType.CLIENT)
public class DuatDimensionEffects extends DimensionEffects
{
    public DuatDimensionEffects()
    {
        super(Float.NaN, true, SkyType.NORMAL, false, true);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight)
    {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public boolean useThickFog(int camX, int camY)
    {
//        long t = MinecraftClient.getInstance().world.getTime();
//        int period = 100;
//        return t - Math.floor((double) t / period) * period < (double) period / 2;
        return false;
    }
}