package com.neep.neepmeat.util;

import java.util.Arrays;

public class Bezier
{
    protected static long[][] PASCAL_TRIANGLE = pascal(10);

    protected static long[][] pascal(int rows)
    {
        long[][] triangle = new long[rows][];

        for (int row = 1; row <= rows; row++)
        {
            triangle[row - 1] = new long[row];
            int k = 1;
            for (int col = 1; col <= row; col++)
            {
                triangle[row - 1][col - 1] = k;
                k = k * (row - col) / col;
            }
        }
        return triangle;
    }

    public static double bezier(int n, double t, double... weights)
    {
        var sum = 0;
        for(int k = 0; k < n; k++)
        {
            sum += binomial(n, k) * Math.pow(1-t, n-k) * Math.pow(t, k) * weights[k];
        }
        return sum;
    }

    public static double bezier2(double t, double... weights)
    {
        double t2 = t * t;
        double mt = 1 - t;
        double mt2 = mt * mt;
        return
                weights[0] * mt2
                + weights[1] * 2 * mt * t
                + weights[2] * t2;
    }

    public static double bezier3(double t, double... weights)
    {
        double t2 = t * t;
        double t3 = t2 * t;
        double mt = 1 - t;
        double mt2 = mt * mt;
        double mt3 = mt2 * mt;
        return
                weights[0] * mt3
                + weights[1] * 3 * mt2 * t
                + weights[2] * 3 * mt * t2
                + weights[3] * t3;
    }

    public static double derivative3(double t, double... weights)
    {
        return bezier2(t,
                3 * (weights[1] - weights[0]),
                3 * (weights[2] - weights[1]),
                3 * (weights[3] - weights[2]));
    }

    protected static long binomial(int n, int r)
    {
        if (n >= PASCAL_TRIANGLE.length || r >= PASCAL_TRIANGLE[n].length)
        {
            throw new IllegalArgumentException();
        }
        return PASCAL_TRIANGLE[n][r];
    }
}
