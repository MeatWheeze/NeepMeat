package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class PowerUtils
{
    /**
     * Simulates friction and windage losses in an *unloaded* motor. It's here to avoid divide-by-zero issues when calculating rotor speed.
     */
    public static final float MOTOR_TORQUE_LOSS = 300f;

    /**
     * The number of energy units that corresponds to one tick of progress through a recipe. This means that
     * one per-unit unit of energy corresponds to a unit increment in a machine's recipe counter.
     */
    public static final long BASE_POWER = referencePower();
    public static final double DROPLET_POWER = dropletPower();

    /**
     * The name of the power unit used can be defined in lang, but the default is eJ/t (esoteric joules per tick)
     */
    public static final Text POWER_UNIT = Text.translatable("message." + NeepMeat.NAMESPACE + ".power_unit");
    public static final Text POWER = Text.translatable("message." + NeepMeat.NAMESPACE + ".power");
    public static final DecimalFormat POWER_FORMAT = new DecimalFormat("###.##");

    public static double perUnitToAbsolute(double perUnit)
    {
        return Math.round(perUnit * BASE_POWER);
    }

    public static double absoluteToPerUnit(long abs)
    {
        return (double) abs / BASE_POWER;
    }

    public static double perUnitToAbsWatt(double perUnit)
    {
        return perUnitToAbsolute(perUnit) * 20;
    }

    public static MutableText perUnitToText(double perUnit)
    {
        return POWER.copyContentOnly().append(Text.literal(POWER_FORMAT.format(perUnitToAbsolute(perUnit))).append(POWER_UNIT));
    }

    public static long absToAmount(Fluid fluid, long energy)
    {
        FluidEnegyRegistry.Entry entry = FluidEnegyRegistry.getInstance().getOrEmpty(fluid);
        if (entry.baseEnergy() == 0) return 0;
        return (long) Math.floor(energy / entry.baseEnergy());
    }

    public static long amountToAbsEnergy(long amount, Fluid fluid)
    {
        FluidEnegyRegistry.Entry entry = FluidEnegyRegistry.getInstance().getOrEmpty(fluid);
        return (long) (entry.baseEnergy() * amount);
    }

    /**
     * @param fluidVariant Fluid variant to query
     * @param amountTransferred Fluid amount in droplets
     * @param dt Time over which the energy is dissipated in ticks
     * @return The average power supplied by the moving fluid
     */
    public static long fluidPower(@Nullable FluidVariant fluidVariant, long amountTransferred, int dt)
    {
        if (fluidVariant == null || amountTransferred == 0 || dt == 0) return 0;
        FluidEnegyRegistry.Entry entry = FluidEnegyRegistry.getInstance().getOrEmpty(fluidVariant.getFluid());
        return (long) (entry.baseEnergy() * amountTransferred / dt);
    }


    /**
     * To avoid stupidly large numbers (the order of magnitude of 1e14) power will be given per bucket rather than per droplet.
     * I will hope that the resulting floating point imprecision will not have a significant effect.
     * @return The power given by one bucket per tick of water flowing through a 1mx1m area in NEEP Customary Units.
     */
    public static long referencePower()
    {
    /*
     * Bernoulli's equation:
     * 0 = p_0 + 1/2*rho*v^2 + rho*g*h
     *
     * Pressure is equivalent to energy per unit volume. Multiplying by volumetric flow to get power:
     *
     * P = Q(p_0 + 1/2*rho*v^2 + rho*g*h)
     *
     * Neglecting static and hydrostatic pressure, power is:
     *
     * P = Q(1/2*rho*v^2)
     *
     * Volumetric flow: Q = v*A , v = Q/A
     *
     * P = 1/2*Q*(Q/A)^2*rho = 1/2 * Q^3/A^2 * rho
     *
     * Parameters: A = 1, rho = 1000.
     */
//        double Q = 1;

//      Density of water
//        double density = 1000f;

//      Pipe cross-sectional area
//        double A = 1f;

        // Arbitrary constant that makes the result a user-friendly number.
//        double C = 2;

//        return (long) (1d/2d * (Q*Q*Q)/(A*A) * density * C);

        // Okay fine. Let's just make it 1000. A nice round number.
        return 1000;
    }

    public static double dropletPower()
    {
        return referencePower() / (double) FluidConstants.BUCKET;
    }
}
