package com.neep.neepmeat.plc.program;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.plc.PLC;
import com.neep.neepmeat.plc.opcode.InstructionProvider;
import net.minecraft.nbt.NbtCompound;

public interface PLCInstruction extends NbtSerialisable
{

    boolean canStart(PLC plc);

    void start(PlcProgram program, PLC plc);

    InstructionProvider getProvider();

    PLCInstruction EMPTY = new EmptyInstruction();

    static PLCInstruction end() { return EMPTY; }

    class EmptyInstruction implements PLCInstruction
    {
        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            return null;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {

        }

        @Override
        public boolean canStart(PLC plc)
        {
            return false;
        }

        @Override
        public void start(PlcProgram program, PLC plc)
        {

        }

        @Override
        public InstructionProvider getProvider()
        {
            return null;
        }
    }
}
