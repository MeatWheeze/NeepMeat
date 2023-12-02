package com.neep.neepmeat.plc.editor;

import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.PLCState;
import com.neep.neepmeat.plc.instruction.*;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class ImmediateState implements PLCState
{
    private final PLCBlockEntity parent;

//    @Nullable private ImmediateInstruction instruction;
    @Nullable private InstructionBuilder instructionBuilder;

    public ImmediateState(PLCBlockEntity plc)
    {
        this.parent = plc;
    }

    @Override
    public void setInstructionBuilder(InstructionProvider provider)
    {
        instructionBuilder = provider.start((ServerWorld) parent.getWorld(), this::emitInstruction);
//        if (provider instanceof ImmediateInstructionProvider immediate)
//        {
//            instruction = immediate.createImmediate(parent::getWorld);
//        }
    }

    private void emitInstruction(Instruction instruction)
    {
        parent.resetError();

        if (parent.notExecuting())
            parent.execute(instruction);
    }

    @Override
    public void argument(Argument argument)
    {
//        if (instruction != null)
//        {
//            instruction.argument(argument, parent);
//            if (instruction.isFinished())
//            {
//                instruction = null;
//            }
//        }
        if (instructionBuilder != null)
        {
            instructionBuilder.argument(argument);
        }
    }

    class Client implements PLCState
    {
        @Override
        public void setInstructionBuilder(InstructionProvider provider)
        {

        }

        @Override
        public void argument(Argument argument)
        {

        }
    }
}
