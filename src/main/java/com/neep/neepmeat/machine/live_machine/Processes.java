package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.Process;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Processes
{
    private static final Processes INSTANCE = new Processes();

    public static Processes getInstance()
    {
        return INSTANCE;
    }

//    private final Map<BitSet, Process> entries = new HashMap<>();

    private final List<Pair<BitSet, Process>> entries = new ObjectArrayList<>();

    public void register(Process process)
    {
        register(process, process.getRequired().toArray(new ComponentType<?>[0]));
    }

    public void register(BitSet bitSet, Process process)
    {
        entries.add(Pair.of(bitSet, process));
//        entries.put(bitSet, process);
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
        for (var pair : entries)
        {
            BitSet copy = (BitSet) pair.key().clone();
            copy.and(bits);
            if (copy.cardinality() == pair.key().cardinality())
            {
                return pair.value();
            }
        }
        return null;
    }

    public static void init()
    {
        getInstance().register(new CrusherProcess());
        getInstance().register(new LargeTrommelProcess());
    }
}
