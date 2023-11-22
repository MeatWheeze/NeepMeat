package com.neep.neepmeat.plc.program;

import com.google.common.collect.Lists;

import java.util.List;

public class PLCProgramImpl implements PlcProgram
{
    protected List<PLCInstruction> instructions = Lists.newArrayList();

    public PLCProgramImpl()
    {

    }

    public void add(PLCInstruction instruction)
    {
        instructions.add(instruction);
    }

    @Override
    public PLCInstruction get(int index)
    {
        if (index < instructions.size())
        {
            return instructions.get(index);
        }
        return PLCInstruction.empty();
    }
}
