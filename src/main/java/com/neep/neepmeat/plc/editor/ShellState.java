package com.neep.neepmeat.plc.editor;

import com.neep.neepmeat.api.plc.instruction.*;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.network.plc.PLCErrorMessageS2C;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.PLCState;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class ShellState implements PLCState
{
    private final PLCBlockEntity parent;

    @Nullable private InstructionBuilder instructionBuilder;
    @Nullable private InstructionProvider provider;

    public ShellState(PLCBlockEntity plc)
    {
        this.parent = plc;
    }

    @Override
    public void setInstructionBuilder(InstructionProvider provider)
    {
        instructionBuilder = provider.start((ServerWorld) parent.getWorld(), this::emitInstruction);
        this.provider = provider;
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
        if (instructionBuilder != null)
        {
            try
            {
                instructionBuilder.argument(argument);
            }
            catch (InstructionException e)
            {
                PLCErrorMessageS2C.send((ServerPlayerEntity) parent.getSurgeryRobot().getController(), e.getMessage());
            }
        }
    }

    @Override
    public RecordMode getMode()
    {
        return RecordMode.IMMEDIATE;
    }

    @Override
    public int getArgumentCount()
    {
        if (instructionBuilder == null)
            return 0;

        return instructionBuilder.argumentCount();
    }

    @Override
    public int getMaxArguments()
    {
        if (provider == null)
            return 0;
        return provider.maxArguments();
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
