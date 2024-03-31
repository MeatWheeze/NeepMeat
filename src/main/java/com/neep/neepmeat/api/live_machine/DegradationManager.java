package com.neep.neepmeat.api.live_machine;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.function.Supplier;

public class DegradationManager implements NbtSerialisable
{
    private final Supplier<Float> degradationRate;
    private final Random random;
    private float degradation = 0;

    public DegradationManager(Supplier<Float> degradationRate, Random random)
    {
        this.degradationRate = degradationRate;
        this.random = random;
    }

    public float getDegradation()
    {
        return degradation;
    }

    public void tick()
    {
        degradation = MathHelper.clamp(
                degradation + (random.nextFloat() * degradationRate.get()),
                0, 1);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putFloat("degradation", degradation);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.degradation = nbt.getFloat("degradation");
    }
}
