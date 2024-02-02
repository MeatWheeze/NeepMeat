package com.neep.neepmeat.api.plc.program;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.neepasm.program.Program;
import com.neep.neepmeat.plc.instruction.Instruction;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public interface PlcProgram extends Program, NbtSerialisable
{
    Empty EMPTY = new Empty();

    class Empty implements PlcProgram
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
        public @Nullable Label findLabel(String label)
        {
            return null;
        }
    }
}