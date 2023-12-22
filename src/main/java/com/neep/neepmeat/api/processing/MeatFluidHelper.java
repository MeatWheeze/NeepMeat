package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class MeatFluidHelper
{
    protected static final String KEY_ROOT = NeepMeat.NAMESPACE + "food";
    protected static final String KEY_HUNGER = "hunger";
    protected static final String KEY_SATURATION = "saturation";
    public static final int MAX_HUNGER = 10;

    public static int getHunger(FluidVariant variant)
    {
        NbtCompound root = getRoot(variant);
        if (root != null)
        {
            return MathHelper.clamp(root.getInt(KEY_HUNGER), 0, MAX_HUNGER);
        }
        return -1;
    }

    public static void setHunger(FluidVariant variant)
    {

    }

    public static float getSaturation(FluidVariant variant)
    {
        NbtCompound root = getRoot(variant);
        if (root != null)
        {
            return  root.getFloat(KEY_SATURATION);
        }
        return -1;
    }

    public static void setSaturation(FluidVariant variant)
    {
    }

    protected static NbtCompound getRoot(FluidVariant variant)
    {
        if (variant.hasNbt()) return variant.getNbt().getCompound(KEY_ROOT);
        return null;
    }

    public static int getColour(FluidVariant variant)
    {
        int hunger = getHunger(variant);
        int r = 255 * hunger / MAX_HUNGER;
        return r << 16;
    }

    public static FluidVariant getVariant(FoodComponent food)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(KEY_HUNGER, food.getHunger());
        nbt.putFloat(KEY_SATURATION, food.getSaturationModifier());
        NbtCompound root = new NbtCompound();
        root.put(KEY_ROOT, nbt);
        return FluidVariant.of(NMFluids.STILL_C_MEAT, root);
    }
}
