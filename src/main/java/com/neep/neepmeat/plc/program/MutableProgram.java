package com.neep.neepmeat.plc.program;

public interface MutableProgram extends PlcProgram
{
    void addBack(PLCInstruction instruction);

    void insert(int index, PLCInstruction instruction);

    void remove(int index);
}
