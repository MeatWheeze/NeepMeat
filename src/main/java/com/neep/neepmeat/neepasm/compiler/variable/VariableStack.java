package com.neep.neepmeat.neepasm.compiler.variable;

import com.neep.neepmeat.api.plc.PLC;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class VariableStack implements Stack<Variable<?>>
{
    private final ObjectArrayList<Variable<?>> entries = new ObjectArrayList<>();

    private final PLC plc;
    private final int maxSize;

    public VariableStack(PLC plc, int maxSize)
    {
        this.plc = plc;
        this.maxSize = maxSize;
    }

    @Override
    public void push(Variable<?> entry)
    {
        if (size() >= maxSize)
        {
            plc.raiseError(new PLC.Error("Variable stack overflow"));
            return;
        }

        entries.push(entry);
    }

    @Override
    public Variable<?> pop()
    {
        if (isEmpty())
        {
            plc.raiseError(new PLC.Error("Variable stack underflow"));
            return Variable.EMPTY;
        }
        return entries.pop();
    }

    @Override
    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    @Override
    public Variable<?> top()
    {
        return entries.top();
    }

    @Override
    public Variable<?> peek(int i)
    {
        return entries.peek(i);
    }

    public int size()
    {
        return entries.size();
    }
}
