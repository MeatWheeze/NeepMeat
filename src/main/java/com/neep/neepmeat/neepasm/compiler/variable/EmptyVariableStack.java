package com.neep.neepmeat.neepasm.compiler.variable;

import it.unimi.dsi.fastutil.Stack;

public class EmptyVariableStack implements Stack<Variable<?>>
{
    @Override
    public void push(Variable<?> variable)
    {

    }

    @Override
    public Variable<?> peek(int i)
    {
        return Variable.EMPTY;
    }

    @Override
    public Variable<?> pop()
    {
        return Variable.EMPTY;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }
}
