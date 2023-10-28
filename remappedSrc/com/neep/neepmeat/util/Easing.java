package com.neep.neepmeat.util;

import D;

public class Easing
{
    public static double easeInOutBack(double x)
    {
        var c1 = 1.70158;
        var c2 = c1 * 1.525;

        return x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    public static double easeOutBack(double x)
    {
        var c1 = 1.70158;
        var c3 = c1 + 1;

        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    }

    public static double easeInBack(double x)
    {
        var c1 = 1.70158;
        var c3 = c1 + 1;

        return c3 * x * x * x - c1 * x * x;

    }

    @FunctionalInterface
    public interface Curve
    {
        double apply(double x);
    }
}
