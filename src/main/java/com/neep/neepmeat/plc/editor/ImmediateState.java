package com.neep.neepmeat.plc.editor;

import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.PLCState;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.ImmediateInstruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import org.jetbrains.annotations.Nullable;

public class ImmediateState implements PLCState
{
    private final PLCBlockEntity parent;

    @Nullable private ImmediateInstruction instruction;

    public ImmediateState(PLCBlockEntity plc)
    {
        this.parent = plc;
    }

    @Override
    public void setInstructionBuilder(InstructionProvider provider)
    {
        if (provider instanceof InstructionProvider.Immediate immediate)
        {
            instruction = immediate.createImmediate(parent::getWorld);
        }
    }

    @Override
    public void argument(Argument argument)
    {
        if (instruction != null)
        {
            instruction.argument(argument, parent);
        }
    }
}
