package com.neep.neepmeat.api.live_machine;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class DegradationManager implements NbtSerialisable
{
    private final RateFunction degradationRate;
    private double degradation = 0f;

    public DegradationManager(RateFunction degradationRate, Random random)
    {
        this.degradationRate = degradationRate;
    }

    public float getDegradation()
    {
        return (float) degradation;
    }

    public long estimateRul()
    {
        double rate = degradationRate.get(degradation);
        if (rate <= 0 )
            return -1;

        return (long) ((1 - degradation) / rate);
    }

    public void tick()
    {
        double rate = degradationRate.get(degradation);
        degradation = MathHelper.clamp(
                degradation + rate,
                0, 1);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putDouble("degradation", degradation);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.degradation = nbt.getFloat("degradation");
    }

    public void subtract(float amount)
    {
        degradation = MathHelper.clamp(degradation - amount, 0, 1);
    }

    @FunctionalInterface
    public interface RateFunction
    {
        double get(double degradation);
    }
}
