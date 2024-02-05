package com.neep.neepmeat.api.plc.program;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.neepasm.program.Program;
import com.neep.neepmeat.plc.instruction.Instruction;
import net.minecraft.nbt.NbtCompound;

public interface PLCProgram extends Program, NbtSerialisable
{
    Empty EMPTY = new Empty();

    class Empty implements PLCProgram
    {
        @Override
        public NbtCompound writeNbt(NbtCompound nbt) { return nbt; }

        @Override
        public void readNbt(NbtCompound nbt) { }

        @Override
        public Instruction get(int index) { return Instruction.EMPTY; }

        @Override
        public int size() { return 0; }

        @Override
        public int getDebugLine(int counter)
        {
            return -1;
        }

    }
}