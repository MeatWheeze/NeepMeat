package com.neep.neepmeat.neepasm.program;

public class Label
{
    private final String name;
    private final int index;

    public Label(String name, int index)
    {
        this.name = name;
        this.index = index;
    }

    public String name()
    {
        return name;
    }

    public int index()
    {
        return index;
    }

    public enum Seek
    {
        FORWARDS,
        BACKWARDS,
        ABSOLUTE
    }
}
