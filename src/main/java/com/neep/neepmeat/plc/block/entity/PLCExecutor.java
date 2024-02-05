package com.neep.neepmeat.plc.block.entity;

import com.neep.neepmeat.plc.instruction.Instruction;

public interface PLCExecutor
{
    void receiveInstruction(Instruction instruction);
}
