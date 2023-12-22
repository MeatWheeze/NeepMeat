package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.MathHelper;

public class MeatFluidHelper
{
    protected static final String KEY_ROOT = NeepMeat.NAMESPACE + "food";
    protected static final String KEY_HUNGER = "hunger";
    protected static final String KEY_SATURATION = "saturation";
    public static final int MAX_HUNGER = 10;

    public static float getHunger(FluidVariant variant)
    {
        NbtCompound root = getRoot(variant);
        if (root != null)
        {
            return MathHelper.clamp(root.getFloat(KEY_HUNGER), 0, MAX_HUNGER);
        }
        return 0;
    }

    public static void setHunger(NbtCompound nbt, float hunger)
    {
        NbtCompound root = getOrCreateRoot(nbt);
        root.putFloat(KEY_HUNGER, hunger);
    }

    public static void setSaturation(NbtCompound nbt, float saturation)
    {
        NbtCompound root = getOrCreateRoot(nbt);
        root.putFloat(KEY_SATURATION, saturation);
    }

    public static float getSaturation(FluidVariant variant)
    {
        NbtCompound root = getRoot(variant);
        if (root != null)
        {
            return  root.getFloat(KEY_SATURATION);
        }
        return 0;
    }

    public static void setSaturation(FluidVariant variant)
    {
    }

    protected static NbtCompound getRoot(FluidVariant variant)
    {
        if (variant.hasNbt()) return variant.getNbt().getCompound(KEY_ROOT);
        return null;
    }

    protected static NbtCompound getOrCreateRoot(NbtCompound nbt)
    {
        if (nbt.contains(KEY_ROOT, NbtCompound.COMPOUND_TYPE)) return nbt.getCompound(KEY_ROOT);
        else
        {
            NbtCompound newRoot = new NbtCompound();
            nbt.put(KEY_ROOT, newRoot);
            return newRoot;
        }
    }

    public static int getColour(FluidVariant variant)
    {
        float hunger = getHunger(variant);
        int r = (int) (255 * hunger / MAX_HUNGER);
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
