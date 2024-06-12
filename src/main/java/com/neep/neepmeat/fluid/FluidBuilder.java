package com.neep.neepmeat.fluid;

public class FluidBuilder
{
    private final String namespace;
    private final String baseName;

    private int levelDecrease = 2;
    private int tickRate = 5;
    private boolean isInfinite = false;

    private boolean makeItem = false;
    private boolean makeBlock = false;

    public FluidBuilder(String namespace, String baseName)
    {
        this.namespace = namespace;
        this.baseName = baseName;
    }

    public FluidBuilder levelDecrease(int levelDecrease)
    {
        this.levelDecrease = levelDecrease;
        return this;
    }

    public FluidBuilder tickRate(int tickRate)
    {
        this.tickRate = tickRate;
        return this;
    }

    public FluidBuilder isInfinite(boolean isInfinite)
    {
        this.isInfinite = isInfinite;
        return this;
    }

    public FluidBuilder withItem()
    {
        makeItem = true;
        return this;
    }

    public FluidBuilder withBlock()
    {
        makeBlock = true;
        return this;
    }

    public BuiltFluid build()
    {
        return new BuiltFluid(namespace, baseName, levelDecrease, tickRate, makeBlock, makeItem);
    }
}
