package com.neep.neepmeat.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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

    public static class Cubic3
    {
        protected double[][] points = new double[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        protected boolean sampled = false;
        protected double length;
        protected int samples;
        protected Vec3d[] distanceSamples;
        private double[] distanceToT;

        public Cubic3(int samples)
        {
            this.samples = samples;
        }

        public void setPoint(int point, double... weights)
        {
            points[point] = weights;
            sampled = false;
        }

        public Vec3d value(double t)
        {
            return new Vec3d(
                    bezier3(t, points[0][0], points[1][0], points[2][0], points[3][0]),
                    bezier3(t, points[0][1], points[1][1], points[2][1], points[3][1]),
                    bezier3(t, points[0][2], points[1][2], points[2][2], points[3][2])
            );
        }

        public double tForDistance(double distance)
        {
//            return distanceToT[(int) Math.round(samples * distance / length())];
            return arrayInterpolate(distanceToT, samples * distance / length());
        }

        public Vec3d derivative(double t)
        {
            return new Vec3d(
                    derivative3(t, points[0][0], points[1][0], points[2][0], points[3][0]),
                    derivative3(t, points[0][1], points[1][1], points[2][1], points[3][1]),
                    derivative3(t, points[0][2], points[1][2], points[2][2], points[3][2])
            );
        }

        public double length()
        {
            if (!sampled) estimateLength();

            return length;
        }

        protected void estimateLength()
        {
            distanceToT = new double[samples + 1];
            double[] ts = new double[samples + 1];
            double[] lengths = new double[samples + 1];

            double tInterval = 1 / (double) samples;
            int i = 0;
            Vec3d prevPoint = value(0);
            for (double t = 0; t <= 1; ++i, t += tInterval)
            {
                Vec3d nextPoint = value(t);

                ts[i] = t;
                length += prevPoint.distanceTo(nextPoint);
                lengths[i] = length;

                prevPoint = nextPoint;
            }

            double s = 0;
            double sInterval = length / samples;
            for (int j = 0; j < ts.length; ++j, s += sInterval)
            {
                distanceToT[j] = thingy(lengths, ts, s);
            }
//
//            double s = 0;
//            for (int j = 0; j < distanceSamples.length; ++j, s += sInterval)
//            {
//
//            }

            sampled = true;
        }

        protected double thingy(double[] xs, double[] ys, double x)
        {
            int j = 0;
            for (int i = 0; i < xs.length; i++)
            {
                if (Math.abs(xs[i] - x) < Math.abs(xs[j] - x))
                {
                    j = i;
                }
            }

            // Find fraction
            double fraction = j < xs.length - 1 ? (x - xs[j]) / (xs[j + 1] - xs[j]) : 0;

            return arrayInterpolate(ys, j + fraction);
        }

        protected double arrayInterpolate(double[] array, double index)
        {
            int lowerIndex = (int) Math.floor(index);
            if (lowerIndex == array.length - 1) return array[lowerIndex];
            int higherIndex = (int) Math.ceil(index);

            return MathHelper.lerp(index - lowerIndex, array[lowerIndex], array[higherIndex]);
        }
    }
}
