package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.PLC;
import com.neep.neepmeat.plc.program.PlcProgram;
import net.minecraft.nbt.NbtCompound;

public interface Instruction extends NbtSerialisable
{
    boolean canStart(PLC plc);

    void start(PlcProgram program, PLC plc);

    InstructionProvider getProvider();

    Instruction EMPTY = new EmptyInstruction();

    static Instruction end() { return EMPTY; }

    class EmptyInstruction implements Instruction
    {
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
            plc.setCounter(-1);
        }

        @Override
        public InstructionProvider getProvider()
        {
            return Instructions.END;
        }
    }
}
