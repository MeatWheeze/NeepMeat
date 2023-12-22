package com.neep.neepmeat.plc.editor;

import com.neep.neepmeat.api.plc.instruction.Argument;
import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.PLCState;
import com.neep.neepmeat.plc.instruction.*;
import net.minecraft.nbt.NbtCompound;
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
        parent.stop();

        if (parent.notExecuting())
        {
            parent.setCounter(0);
            parent.execute(instruction);
        }
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

    @Override
    public RecordMode getMode()
    {
        return RecordMode.IMMEDIATE;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}
