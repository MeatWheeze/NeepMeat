package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.MathHelper;

public class MeatFluidHelper
{
    protected static final String KEY_ROOT = NeepMeat.NAMESPACE + "food";
    protected static final String KEY_HUNGER = "hunger";
    protected static final String KEY_SATURATION = "saturation";
    public static final int MAX_HUNGER = 10;

    public static int getHunger(FluidVariant variant)
    {
        if (variant.hasNbt())
        {
            return MathHelper.clamp(variant.getNbt().getCompound(KEY_ROOT).getInt(KEY_HUNGER), 0, MAX_HUNGER);
        }
        return -1;
    }

    public static int getSaturation(FluidVariant variant)
    {
        if (variant.hasNbt())
        {
            return  variant.getNbt().getCompound(KEY_ROOT).getInt(KEY_SATURATION);
        }
        return -1;
    }

    public static int getColour(FluidVariant variant)
    {
        int hunger = getHunger(variant);
        int r = 255 * hunger / MAX_HUNGER;
        return r << 16;
    }
}
