package com.neep.neepmeat.machine.live_machine;

import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Processes
{
    private final Map<BitSet, Entry> entries = new HashMap<>();

    @Nullable
    public Entry getFirstMatch(BitSet bits)
    {
        return null;
    }

    public static class Entry
    {

    }
}
