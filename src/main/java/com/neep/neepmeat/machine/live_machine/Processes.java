package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.Process;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Processes
{
    private static final Processes INSTANCE = new Processes();

    public static Processes getInstance()
    {
        return INSTANCE;
    }

    private final Map<BitSet, Process> entries = new HashMap<>();

    public void register(Process process)
    {
        register(process, process.getRequired().toArray(new ComponentType<?>[0]));
    }

    public void register(BitSet bitSet, Process process)
    {
        entries.put(bitSet, process);
    }

    public void register(Process process, ComponentType<?>... types)
    {
        BitSet bitSet = new BitSet();
        for (ComponentType<?> type : types)
        {
            bitSet.set(type.getBitIdx());
        }
        register(bitSet, process);
    }

    @Nullable
    public Process getFirstMatch(BitSet bits)
    {
        return entries.get(bits);
    }

    public static void init()
    {
        getInstance().register(new CrusherProcess());
    }
}
