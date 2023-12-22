package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.program.PlcProgram;
import net.minecraft.nbt.NbtCompound;

public class RestartInstruction implements Instruction
{
    public static final RestartInstruction INSTANCE = new RestartInstruction();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public boolean canStart(PLC plc)
    {
        return true;
    }

    @Override
    public void start(PlcProgram program, PLC plc)
    {
        plc.setCounter(0);
    }

    @Override
    public InstructionProvider getProvider()
    {
        return Instructions.GOTO_START;
    }
}
