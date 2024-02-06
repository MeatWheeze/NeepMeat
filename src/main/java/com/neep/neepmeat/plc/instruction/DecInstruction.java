package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.plc.Instructions;
import org.jetbrains.annotations.NotNull;

public class DecInstruction implements Instruction
{
    @Override
    public void start(PLC plc)
    {
//        Variable<?> v1 = plc.variableStack().pop();
//        if (v1 instanceof IntVariable iv)
//        {
//            plc.variableStack().push(new IntVariable(iv.value() - 1));
//            plc.advanceCounter();
//            return;
//        }
//        plc.raiseError(new PLC.Error("Incompatible variable types"));
        int i = plc.variableStack().popInt() - 1;
        plc.variableStack().push(i);
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.DEC;
    }
}
