package com.neep.neepmeat.plc;

import com.neep.neepmeat.plc.opcode.InstructionProvider;
import com.neep.neepmeat.plc.program.CombineInstruction;
import com.neep.neepmeat.plc.program.PLCInstruction;
import net.minecraft.text.Text;

public class Instructions
{
    public static final InstructionProvider END = new InstructionProvider((w, a) -> PLCInstruction.end(), 0, Text.of("END"));
    public static final InstructionProvider COMBINE = new InstructionProvider(CombineInstruction::new, 2, Text.of("COMBINE"));
}
